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
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.bind.Jsonb;
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
	
	private final Jsonb jsonb;
	
	private GameDownloadPlan plan;
	private final GameDownloadPlan.Repository planRepository;
	private final Game.Repository gameRepository;
	private final Installer.Repository installerRepository;
	private final GameExtra.Repository gameExtraRepository;
	private final GameMetadata.Repository metadataRepository;
	
	public DownloadGameTask(GameDownloadPlan plan, UserToken userToken, Jsonb jsonb, GameDownloadPlan.Repository planRepository, Game.Repository gameRepository, Installer.Repository installerRepository, GameExtra.Repository gameExtraRepository, GameMetadata.Repository metadataRepository) {
		this.plan = plan;
		this.userToken = userToken;
		this.jsonb = jsonb;
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
			
			
			Game game = gameRepository.findByTitle(details.title()).orElseGet(() -> {
				// See if we have an image for it in our metadata and download if so
				List<EntityAttachment> attachments = new ArrayList<>();
				AtomicReference<Game> gameRef = new AtomicReference<Game>(null);
				metadataRepository.findByGameId(ViewQuery.query().key(plan.getGameId(), true)).ifPresentOrElse(metadata -> {
					if(StringUtil.isNotEmpty(metadata.imageUrl())) {
						download(authToken, metadata.imageUrl() + ".webp", p -> {
							p.getFileName().toString();
							attachments.add(EntityAttachment.of(p));
							Game newGame = new Game(null, details.title(), plan.getGameId(), p.getFileName().toString(), attachments);
							gameRef.set(gameRepository.save(newGame, true));
						});
					}
				}, () -> {
					Game newGame = new Game(null, details.title(), plan.getGameId(), null, null);
					gameRef.set(gameRepository.save(newGame, true));
				});
				
				return gameRef.get();
				
			});
			
			plan.setGameDocumentId(game.documentId());
			plan.setState(GameDownloadPlan.State.InProgress);
			plan = planRepository.save(plan, true);
			
			details.extras().forEach(extra -> this.downloadExtra(authToken, game.documentId(), extra));
			
			JsonArray downloads = details.downloads();
			String language = null;
			for(JsonValue value : downloads) {
				if(value.getValueType() == ValueType.ARRAY) {
					for(JsonValue innerValue : value.asJsonArray()) {
						if(innerValue.getValueType() == ValueType.STRING) {
							language = ((JsonString)innerValue).getString();
						} else if(innerValue.getValueType() == ValueType.OBJECT) {
							// Then it's a Map of OS -> Download[]
							JsonObject obj = innerValue.asJsonObject();
							for(Map.Entry<String, JsonValue> entry : obj.entrySet()) {
								String os = entry.getKey();
								for(JsonValue downloadValue : entry.getValue().asJsonArray()) {
									GameDownload download = jsonb.fromJson(downloadValue.asJsonObject().toString(), GameDownload.class);
									
									String fLanguage = language;
									this.downloadInstaller(authToken, game.documentId(), fLanguage, os, download);
								}
							}
						}
					}
				}
			}
			
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

	private void downloadInstaller(String authToken, String gameDocumentId, String language, String os, GameDownload download) {
		download(authToken, download.manualUrl(), tempFile -> {
			Installer installer = new Installer(null, gameDocumentId, download.name(), language, os, List.of(EntityAttachment.of(tempFile)));
			installer = installerRepository.save(installer, true);
			
			plan.addInstaller(installer);
			plan = planRepository.save(plan, true);
		});
	}
	
	private void downloadExtra(String authToken, String gameDocumentId, api.gog.model.GameExtra extra) {
		download(authToken, extra.manualUrl(), tempFile -> {
			GameExtra gameExtra = new GameExtra(null, gameDocumentId, extra.name(), extra.type(), List.of(EntityAttachment.of(tempFile)));
			gameExtra = gameExtraRepository.save(gameExtra, true);
			
			plan.addExtra(gameExtra);
			plan = planRepository.save(plan, true);
		});
	}
	

	private void download(String authToken, String manualUrl, Consumer<java.nio.file.Path> callback) {
		try(HttpClient http = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build()) {
			URI baseUri = URI.create("https://gog.com/");
			
			URI attUri = baseUri.resolve(manualUrl);
			System.out.println("downloading " + attUri);
			
			HttpRequest req = HttpRequest.newBuilder(attUri)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
				.GET()
				.build();
			try {
				HttpResponse<InputStream> resp = http.send(req, BodyHandlers.ofInputStream());
				String finalUri = resp.uri().toString();
				int slashIndex = finalUri.lastIndexOf('/');
				String fileName = finalUri.substring(slashIndex+1)
					.replace('%', '_')
					.replace('/', '_')
					.replace('\\', '_');
				
				java.nio.file.Path tempDir = Files.createTempDirectory(getClass().getName());
				java.nio.file.Path tempFile = tempDir.resolve(fileName);
				
				try {
					try(InputStream is = resp.body()) {
						Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
					}
					
					callback.accept(tempFile);
				} finally {
					Files.deleteIfExists(tempFile);
					Files.deleteIfExists(tempDir);
				}
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}