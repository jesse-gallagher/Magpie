package tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import com.ibm.commons.util.StringUtil;

import api.gog.GogAccountApi;
import api.gog.GogAuthApi;
import api.gog.GogAuthApi.GrantType;
import api.gog.model.GameDetails;
import api.gog.model.GameDownload;
import api.gog.model.TokenResponse;
import jakarta.ws.rs.core.HttpHeaders;
import model.Game;
import model.GameDownloadPlan;
import model.GameExtra;
import model.GameMetadata;
import model.Installer;
import model.UserToken;

public class DownloadGameTask implements Runnable {
	private static final Logger log = Logger.getLogger(DownloadGameTask.class.getName());
	
	private final UserToken userToken;
	
	private GameDownloadPlan plan;
	private final GameDownloadPlan.Repository planRepository;
	private final Game.Repository gameRepository;
	private final Installer.Repository installerRepository;
	private final GameExtra.Repository gameExtraRepository;
	private final GameMetadata.Repository metadataRepository;
	
	public DownloadGameTask(GameDownloadPlan plan, UserToken userToken, GameDownloadPlan.Repository planRepository, Game.Repository gameRepository, Installer.Repository installerRepository, GameExtra.Repository gameExtraRepository, GameMetadata.Repository metadataRepository) {
		this.plan = plan;
		this.userToken = userToken;
		this.planRepository = planRepository;
		this.gameRepository = gameRepository;
		this.installerRepository = installerRepository;
		this.gameExtraRepository = gameExtraRepository;
		this.metadataRepository = metadataRepository;
	}

	@Override
	public void run() {
		
		try {
			GogAuthApi authApi = RestClientBuilder.newBuilder()
				.baseUri(GogAuthApi.BASE_URI)
				.build(GogAuthApi.class);
			
			TokenResponse response = authApi.getToken(
				GogAuthApi.DEFAULT_CLIENT_ID,
				GogAuthApi.DEFAULT_CLIENT_SECRET,
				GrantType.refresh_token,
				null,
				null,
				userToken.getRefreshToken()
			);
			
			String authToken = response.accessToken();
			
			GogAccountApi accountApi = RestClientBuilder.newBuilder()
				.baseUri(GogAccountApi.BASE_URI)
				.build(GogAccountApi.class);
			
			GameDetails details = accountApi.getGameDetails("Bearer " + authToken, plan.getGameId());
			
			
			Game game = findOrCreateGame(authToken, details);
			
			plan.setGameDocumentId(game.documentId());
			plan.setState(GameDownloadPlan.State.InProgress);
			plan = planRepository.save(plan, true);
			
			details.extras().forEach(extra -> this.downloadExtra(authToken, game.documentId(), extra));
			
			details.getParsedDownloads()
				.forEach(entry -> this.downloadInstaller(authToken, game.documentId(), entry.language(), entry.os(), entry.download()));
			
			plan.setState(GameDownloadPlan.State.Complete);
			plan = planRepository.save(plan, true);
		} catch(Exception e) {
			if(log.isLoggable(Level.SEVERE)) {
				log.log(Level.SEVERE, MessageFormat.format("Encountered exception downloading game ID {0}", Integer.toString(plan.getGameId())), e);
			}
			
			try(StringWriter sw = new StringWriter(); PrintWriter w = new PrintWriter(sw)) {
				e.printStackTrace(w);
				w.flush();
				plan.setStackTrace(sw.toString());
			} catch(IOException e2) {
				// Ignore
			}
			
			plan.setState(GameDownloadPlan.State.Exception);
			planRepository.save(plan, true);
		}
	}
	
	private Game findOrCreateGame(String authToken, GameDetails details) {
		return gameRepository.findByTitle(details.title()).orElseGet(() -> {

			List<Path> tempFiles = new ArrayList<>();
			try {
				// See if we have an image for it in our metadata and download if so
				List<EntityAttachment> attachments = new ArrayList<>();
				
				AtomicReference<String> imageFileName = new AtomicReference<>(null);
				AtomicReference<String> backgroundFileName = new AtomicReference<>(null);
				
				Optional<GameMetadata> gameMetadata = metadataRepository.findByGameId(ViewQuery.query().key(plan.getGameId(), true));
				if(gameMetadata.isPresent()) {
					if(StringUtil.isNotEmpty(gameMetadata.get().imageUrl())) {
						Path p = download(authToken, gameMetadata.get().imageUrl() + ".webp", "image.webp");
						imageFileName.set(p.getFileName().toString());
						attachments.add(EntityAttachment.of(p));
						tempFiles.add(p);
					}
				}
				
				if(StringUtil.isNotEmpty(details.backgroundImage())) {
					Path p = download(authToken, details.backgroundImage() + ".webp", "background.webp");
					backgroundFileName.set(p.getFileName().toString());
					attachments.add(EntityAttachment.of(p));
					tempFiles.add(p);
				}
				
				return gameRepository.save(new Game(null, details.title(), plan.getGameId(), details.cdKey(), imageFileName.get(), backgroundFileName.get(), attachments), true);
			} finally {
				tempFiles.forEach(p -> {
					try {
						Files.deleteIfExists(p);
						Files.deleteIfExists(p.getParent());
					} catch(IOException e) {
						// Ignore
					}
				});
			}
		});
	}

	private void downloadInstaller(String authToken, String gameDocumentId, String language, String os, GameDownload download) {
		downloadAndDelete(authToken, download.manualUrl(), tempFile -> {
			Installer installer = new Installer(null, gameDocumentId, download.name(), language, os, download.manualUrl(), download.version(), download.date(), List.of(EntityAttachment.of(tempFile)));
			installer = installerRepository.save(installer, true);
			
			plan.addInstaller(installer);
			plan = planRepository.save(plan, true);
		});
	}
	
	private void downloadExtra(String authToken, String gameDocumentId, api.gog.model.GameExtra extra) {
		downloadAndDelete(authToken, extra.manualUrl(), tempFile -> {
			GameExtra gameExtra = new GameExtra(null, gameDocumentId, extra.name(), extra.type(), extra.manualUrl(), List.of(EntityAttachment.of(tempFile)));
			gameExtra = gameExtraRepository.save(gameExtra, true);
			
			plan.addExtra(gameExtra);
			plan = planRepository.save(plan, true);
		});
	}
	
	private Path download(String authToken, String manualUrl, String destFileName) {
		try(HttpClient http = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build()) {
			URI baseUri = URI.create("https://gog.com/");
			
			URI attUri = baseUri.resolve(manualUrl);
			
			HttpRequest req = HttpRequest.newBuilder(attUri)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
				.GET()
				.build();
			try {
				HttpResponse<InputStream> resp = http.send(req, BodyHandlers.ofInputStream());
				String finalUri = resp.uri().toString();
				int slashIndex = finalUri.lastIndexOf('/');
				String fileName = destFileName;
				if(StringUtil.isEmpty(fileName)) {
					fileName = finalUri.substring(slashIndex+1)
						.replace('%', '_')
						.replace('/', '_')
						.replace('\\', '_');
				}
				
				java.nio.file.Path tempDir = Files.createTempDirectory(getClass().getName());
				java.nio.file.Path tempFile = tempDir.resolve(fileName);
				
				try(InputStream is = resp.body()) {
					Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
				}
				return tempFile;
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	

	private void downloadAndDelete(String authToken, String manualUrl, Consumer<Path> callback) {
		try {
			Path tempFile = null;
			try {
				tempFile = download(authToken, manualUrl, null);
				
				callback.accept(tempFile);
			} finally {
				if(tempFile != null) {
					Files.deleteIfExists(tempFile);
					Files.deleteIfExists(tempFile.getParent());
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}