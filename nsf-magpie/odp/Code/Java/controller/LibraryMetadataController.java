package controller;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.ibm.commons.util.StringUtil;

import api.igdb.v4.IgdbApi;
import api.igdb.v4.model.IgdbGame;
import api.igdb.v4.model.SearchResult;
import api.twitch.TwitchAuthApi;
import api.twitch.model.OAuthResponse;
import bean.GameDetailsCache;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import model.ClientID;
import model.Game;
import model.GameDetails;

@Controller
@Path("library/metadata")
public class LibraryMetadataController {
	
	@Inject
	private Models models;
	
	@Inject
	private Game.Repository gameRepository;
	
	@Inject
	@RestClient
	private TwitchAuthApi twitchAuthApi;
	
	@Inject
	@RestClient
	private IgdbApi idgbApi;
	
	@Inject
	private ClientID.Repository clientIdRepository;
	
	@Inject
	private GameDetailsCache detailsCache;
	
	@Inject
	private GameDetails.Repository gameDetailsRepository;
	
	@Path("@add")
	@GET
	public String addMetadata(@NotEmpty @QueryParam("game") String gameId, @QueryParam("title") String title) {
		Game game = gameRepository.findById(gameId)
			.orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Unable to find game for ID {0}", gameId)));
		
		ClientID twitchId = clientIdRepository.findByServiceType("twitch")
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Could not find Twitch Client ID"));
		
		OAuthResponse authResponse = twitchAuthApi.getToken(twitchId.getClientId(), twitchId.getClientSecret(), TwitchAuthApi.GrantType.client_credentials);
		
		String token = authResponse.accessToken();
		
		// TODO figure out what the actual syntax is
		String search = StringUtil.isEmpty(title) ? game.title() : title;
		search = search.replace("\"", "");
		List<SearchResult> searchResults = idgbApi.search(twitchId.getClientId(), "Bearer " + token, "fields *; search \"" + search + "\";");
		
		models.put("searchResults", searchResults);
		models.put("gameId", gameId);
		models.put("search", search);
		return "library/metadataAdd.jsp";
	}
	
	@Path("@addSpecific")
	@GET
	public String addMetadataSpecific(@NotEmpty @QueryParam("game") String gameId, @QueryParam("resultId") int resultId) {
		Game game = gameRepository.findById(gameId)
			.orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Unable to find game for ID {0}", gameId)));
		
		ClientID twitchId = clientIdRepository.findByServiceType("twitch")
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Could not find Twitch Client ID"));
		
		OAuthResponse authResponse = twitchAuthApi.getToken(twitchId.getClientId(), twitchId.getClientSecret(), TwitchAuthApi.GrantType.client_credentials);
		
		String token = authResponse.accessToken();
		
		List<IgdbGame> igdbGames = idgbApi.listGames(twitchId.getClientId(), "Bearer " + token, "fields *; where id = " + resultId + ";");
		if(igdbGames.isEmpty()) {
			throw new NotFoundException(MessageFormat.format("Unable to find IGDB game for ID {0}", resultId));
		}
		
		detailsCache.put("igdb-" + resultId, igdbGames.getFirst().toGameDetails(gameId));
		
		models.put("game", game);
		models.put("igdbGame", igdbGames.getFirst());
		models.put("resultId", Integer.toString(resultId));
		
		return "library/metadataAddSpecific.jsp";
	}
	
	@Path("@addSpecific")
	@POST
	public String saveMetadataSpecific(@NotEmpty @FormParam("game") String gameId, @FormParam("resultId") int resultId) {
		GameDetails details = detailsCache.get("igdb-" + resultId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Could not find cached details for ID {0}", resultId)));
		
		gameDetailsRepository.save(details);
		
		return "redirect:library/" + gameId;
	}
}
