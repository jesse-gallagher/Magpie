package controller;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import api.gog.GogAccountApi;
import api.gog.GogAuthApi;
import api.gog.GogAuthApi.GrantType;
import api.gog.model.FilteredProducts;
import api.gog.model.GameDetails;
import api.gog.model.TokenResponse;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import model.Game;
import model.GameDownloadPlan;
import model.GameExtra;
import model.GameMetadata;
import model.Installer;
import model.UserToken;
import tasks.DownloadGameTask;

@Controller
@Path("gog")
public class GogController {

	@Inject
	private Models models;
	
	@Inject
	@RestClient
	private GogAuthApi authApi;
	
	@Inject
	@RestClient
	private GogAccountApi accountApi;
	
	@Inject
	private GameDownloadPlan.Repository gameDownloadPlanRepository;
	
	@Inject
	private GameMetadata.Repository metadataRepository;
	
	@Inject
	private UserToken.Repository tokenRepository;
	
	@Inject
	private Game.Repository gameRepository;
	
	@Inject @Named("java:comp/DefaultManagedExecutorService")
	private ManagedExecutorService exec;
	
	@Path("search")
	@GET
	public String search(@QueryParam("search") String search, @QueryParam("tokenId") String tokenId) {
		UserToken token = tokenRepository.findById(tokenId)
			.orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Could not find token for ID {0}", tokenId)));

		TokenResponse response = authApi.getToken(
			GogAuthApi.DEFAULT_CLIENT_ID,
			GogAuthApi.DEFAULT_CLIENT_SECRET,
			GrantType.refresh_token,
			null,
			null,
			token.getRefreshToken()
		);
		
		String authToken = response.accessToken();
		
		FilteredProducts result = accountApi.getFilteredProducts(
			"Bearer " + authToken,
			GogAccountApi.TYPE_GAME,
			search
		);
		
		// While here, store metadata for found games
		result.products().forEach(product -> {
			Optional<GameMetadata> existing = metadataRepository.findByGameId(ViewQuery.query().key(product.id(), true));
			if(existing.isEmpty()) {
				metadataRepository.save(GameMetadata.forProduct(product), true);
			}
		});
		
		models.put("result", result);
		models.put("tokenId", tokenId);
		
		return "gog/search.jsp";
	}
	
	@Path("game/{game_id}")
	@GET
	public String getGameDetails(@PathParam("game_id") int gameId, @QueryParam("tokenId") String tokenId) {
		UserToken token = tokenRepository.findById(tokenId)
			.orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Could not find token for ID {0}", tokenId)));

		TokenResponse response = authApi.getToken(
			GogAuthApi.DEFAULT_CLIENT_ID,
			GogAuthApi.DEFAULT_CLIENT_SECRET,
			GrantType.refresh_token,
			null,
			null,
			token.getRefreshToken()
		);
		
		String authToken = response.accessToken();
		
		GameDetails details = accountApi.getGameDetails("Bearer " + authToken, gameId);
		
		models.put("gameId", gameId);
		models.put("tokenId", tokenId);
		models.put("details", details);
		
		// Look for existing downloads so they can be skipped in the UI
		Collection<String> downloadedUrls = new HashSet<>();
		gameRepository.findByTitle(details.title()).ifPresent(game -> {
			game.getGameExtras().stream()
				.map(GameExtra::url)
				.forEach(downloadedUrls::add);
			game.getInstallers().stream()
				.map(Installer::url)
				.forEach(downloadedUrls::add);
		});
		
		models.put("downloadedUrls", downloadedUrls);
		
		Optional<GameMetadata> existing = metadataRepository.findByGameId(ViewQuery.query().key(gameId, true));
		models.put("metadata", existing.orElse(null));
		
		return "gog/game.jsp";
	}
	
	@Path("game/@download")
	@POST
	public String downloadGame(@FormParam("gameId") int gameId, @FormParam("tokenId") String tokenId, @FormParam("downloadUrl") List<String> downloadUrls, @FormParam("extraUrl") List<String> extraUrls) {
		
		GameDownloadPlan plan = new GameDownloadPlan();
		plan.setGameId(gameId);
		plan = gameDownloadPlanRepository.save(plan, true);

		UserToken token = tokenRepository.findById(tokenId)
			.orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Could not find token for ID {0}", tokenId)));
		exec.submit(new DownloadGameTask(plan, token, downloadUrls, extraUrls));
		
		return "redirect:downloads/" + plan.getDocumentId();
	}
	
}
