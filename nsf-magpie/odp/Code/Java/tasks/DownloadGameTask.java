package tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import java.text.MessageFormat;
import java.time.LocalDate;
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

import api.DownloadableFile;
import api.gog.GogAccountApi;
import api.gog.GogAuthApi;
import api.gog.GogAuthApi.GrantType;
import api.gog.model.GameDetails;
import api.gog.model.GameDetails.DownloadEntry;
import api.gog.model.GameDownload;
import api.gog.model.TokenResponse;
import event.DownloadEndEvent;
import event.DownloadProgressEvent;
import event.DownloadStartEvent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.util.TypeLiteral;
import jakarta.ws.rs.core.HttpHeaders;
import model.Game;
import model.GameDownloadPlan;
import model.GameExtra;
import model.GameMetadata;
import model.Installer;
import model.UserToken;
import util.AppUtil;

public class DownloadGameTask implements Runnable {
	private static final Logger log = Logger.getLogger(DownloadGameTask.class.getName());

    private static final int DEFAULT_BUFFER_SIZE = 16384;
	
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
			plan.setExtraUrls(details.extras().stream().map(api.gog.model.GameExtra::manualUrl).toList());
			plan.setInstallerUrls(details.getParsedDownloads().stream().map(DownloadEntry::download).map(GameDownload::manualUrl).toList());
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
				
				String category = "";
				LocalDate releaseDate = null;
				Optional<GameMetadata> gameMetadata = metadataRepository.findByGameId(ViewQuery.query().key(plan.getGameId(), true));
				if(gameMetadata.isPresent()) {
					if(StringUtil.isNotEmpty(gameMetadata.get().imageUrl())) {
						Path p = download(authToken, gameMetadata.get().imageUrl() + ".webp", "image.webp", null);
						imageFileName.set(p.getFileName().toString());
						attachments.add(EntityAttachment.of(p));
						tempFiles.add(p);
					}
					category = gameMetadata.get().category();
					releaseDate = gameMetadata.get().releaseDate();
				}
				
				if(StringUtil.isNotEmpty(details.backgroundImage())) {
					Path p = download(authToken, details.backgroundImage() + ".webp", "background.webp", null);
					backgroundFileName.set(p.getFileName().toString());
					attachments.add(EntityAttachment.of(p));
					tempFiles.add(p);
				}
				
				String sortingTitle = AppUtil.toSortingTitle(details.title());
				if(sortingTitle.equals(details.title())) {
					sortingTitle = null;
				}
				
				return gameRepository.save(new Game(null, details.title(), plan.getGameId(), details.cdKey(), imageFileName.get(), backgroundFileName.get(), category, releaseDate, sortingTitle, null, attachments), true);
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
		@SuppressWarnings("serial")
		Event<DownloadStartEvent> event = CDI.current().select(new TypeLiteral<Event<DownloadStartEvent>>() {}).get();
		event.fire(new DownloadStartEvent(plan, Installer.class, download));
		
		downloadAndDelete(authToken, download.manualUrl(), download, tempFile -> {
			Installer installer = new Installer(null, gameDocumentId, download.name(), language, os, download.manualUrl(), download.version(), download.date(), List.of(EntityAttachment.of(tempFile)));
			installer = installerRepository.save(installer, true);
			
			plan.addInstaller(installer);
			plan = planRepository.save(plan, true);

			@SuppressWarnings("serial")
			Event<DownloadEndEvent> event2 = CDI.current().select(new TypeLiteral<Event<DownloadEndEvent>>() {}).get();
			event2.fire(new DownloadEndEvent(plan, Installer.class, download));
		});
	}
	
	private void downloadExtra(String authToken, String gameDocumentId, api.gog.model.GameExtra extra) {
		@SuppressWarnings("serial")
		Event<DownloadStartEvent> event = CDI.current().select(new TypeLiteral<Event<DownloadStartEvent>>() {}).get();
		event.fire(new DownloadStartEvent(plan, GameExtra.class, extra));
		
		downloadAndDelete(authToken, extra.manualUrl(), extra, tempFile -> {
			GameExtra gameExtra = new GameExtra(null, gameDocumentId, extra.name(), extra.type(), extra.manualUrl(), List.of(EntityAttachment.of(tempFile)));
			gameExtra = gameExtraRepository.save(gameExtra, true);
			
			plan.addExtra(gameExtra);
			plan = planRepository.save(plan, true);

			@SuppressWarnings("serial")
			Event<DownloadEndEvent> event2 = CDI.current().select(new TypeLiteral<Event<DownloadEndEvent>>() {}).get();
			event2.fire(new DownloadEndEvent(plan, GameExtra.class, extra));
		});
	}
	
	private Path download(String authToken, String manualUrl, String destFileName, DownloadableFile contextFile) {
		try(HttpClient http = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build()) {
			URI baseUri = URI.create("https://gog.com/");
			
			URI attUri = baseUri.resolve(manualUrl);
			
			HttpRequest req = HttpRequest.newBuilder(attUri)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
				.GET()
				.build();
			try {

				@SuppressWarnings("serial")
				Event<DownloadProgressEvent> event = CDI.current().select(new TypeLiteral<Event<DownloadProgressEvent>>() {}).get();
				
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
				
				try(
					InputStream is = resp.body();
					OutputStream out = Files.newOutputStream(tempFile);
				) {
					long transferred = 0;
			        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			        int read;
			        while ((read = is.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
			            out.write(buffer, 0, read);
			            if (transferred < Long.MAX_VALUE) {
			                try {
			                    transferred = Math.addExact(transferred, read);
			                } catch (ArithmeticException ignore) {
			                    transferred = Long.MAX_VALUE;
			                }
			                
			                event.fire(new DownloadProgressEvent(plan, contextFile, transferred));
			            }
			        }
				}
				return tempFile;
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	

	private void downloadAndDelete(String authToken, String manualUrl, DownloadableFile contextFile, Consumer<Path> callback) {
		try {
			Path tempFile = null;
			try {
				tempFile = download(authToken, manualUrl, null, contextFile);
				
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