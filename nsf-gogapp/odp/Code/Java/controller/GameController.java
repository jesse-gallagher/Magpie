package controller;

import java.text.MessageFormat;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import api.gog.GogAccountApi;
import api.gog.GogAuthApi;
import api.gog.GogAuthApi.GrantType;
import api.gog.model.GameDetails;
import api.gog.model.TokenResponse;
import bean.UserTokenBean;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.bind.Jsonb;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.Game;
import model.GameDownloadPlan;
import model.GameExtra;
import model.GameMetadata;
import model.Installer;
import model.UserToken;
import tasks.DownloadGameTask;

@Controller
@Path("game")
public class GameController {

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
	private UserTokenBean userTokenBean;
	
	@Inject @Named("java:comp/DefaultManagedExecutorService")
	private ManagedExecutorService exec;
	
	@Inject
	private Jsonb jsonb;
	
	
	@Path("{game_id}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getGameDetails(@PathParam("game_id") int gameId) {
		UserToken token = userTokenBean.getActive().get();

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
		
		models.put("game_id", gameId);
		models.put("details", details);
		
		return "game/game.jsp";
	}
	
	@Path("@download")
	@POST
	@Produces(MediaType.TEXT_HTML)
	public String downloadGame(@FormParam("game_id") int gameId) {
		GameDownloadPlan plan = new GameDownloadPlan();
		plan.setGameId(gameId);
		plan = gameDownloadPlanRepository.save(plan);
		
		UserToken token = userTokenBean.getActive().get();
		exec.submit(new DownloadGameTask(plan, token, jsonb, gameDownloadPlanRepository, gameRepository, installerRepository, gameExtraRepository, metadataRepository));
		
		return "redirect:game/download/" + plan.getDocumentId();
	}
	
	@Path("download/{planId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String showDownloadPlan(@PathParam("planId") String planId) {
		GameDownloadPlan plan = gameDownloadPlanRepository.findById(planId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find download plan for ID {0}", planId)));
		
		models.put("plan", plan);
		
		return "game/downloadPlan.jsp";
	}
	
}
