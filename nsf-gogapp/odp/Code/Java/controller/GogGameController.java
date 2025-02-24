package controller;

import java.text.MessageFormat;
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
import jakarta.ws.rs.NotFoundException;
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
@Path("gog/game")
public class GogGameController {

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
	private Game.Repository gameRepository;
	
	@Inject
	private Installer.Repository installerRepository;
	
	@Inject
	private GameExtra.Repository gameExtraRepository;
	
	@Inject
	private GameMetadata.Repository metadataRepository;
	
	@Inject
	private UserToken.Repository tokenRepository;
	
	@Inject @Named("java:comp/DefaultManagedExecutorService")
	private ManagedExecutorService exec;
	
	@Path("search")
	@POST
	public String search(@FormParam("search") String search, @FormParam("tokenId") String tokenId) {
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
				metadataRepository.save(new GameMetadata(null, product.id(), product.image()), true);
			}
		});
		
		models.put("result", result);
		models.put("tokenId", tokenId);
		
		return "gog/game/search.jsp";
	}
	
	@Path("{game_id}")
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
		
		return "gog/game/game.jsp";
	}
	
	@Path("@download")
	@POST
	public String downloadGame(@FormParam("gameId") int gameId, @FormParam("tokenId") String tokenId) {
		GameDownloadPlan plan = new GameDownloadPlan();
		plan.setGameId(gameId);
		plan = gameDownloadPlanRepository.save(plan, true);

		UserToken token = tokenRepository.findById(tokenId)
			.orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Could not find token for ID {0}", tokenId)));
		exec.submit(new DownloadGameTask(plan, token, gameDownloadPlanRepository, gameRepository, installerRepository, gameExtraRepository, metadataRepository));
		
		return "redirect:gog/game/download/" + plan.getDocumentId();
	}
	
	@Path("download/{planId}")
	@GET
	public String showDownloadPlan(@PathParam("planId") String planId) {
		GameDownloadPlan plan = gameDownloadPlanRepository.findById(planId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find download plan for ID {0}", planId)));
		
		models.put("plan", plan);
		
		return "gog/game/downloadPlan.jsp";
	}
	
}
