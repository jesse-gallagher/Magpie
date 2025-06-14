package controller.source;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.ibm.commons.util.StringUtil;

import api.igdb.v4.IgdbApi;
import api.igdb.v4.model.IgdbGame;
import api.igdb.v4.model.IgdbScreenshot;
import api.igdb.v4.model.SearchResult;
import api.twitch.TwitchAuthApi;
import api.twitch.model.OAuthResponse;
import bean.igdb.IgdbCache;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
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
import model.GameDetails.ScreenshotInfo;
import util.AppUtil;

@Controller
@Path("source/igdb")
public class IgdbController {
	
	@Inject
	private Models models;
	
	@Inject
	private Game.Repository gameRepository;
	
	@Inject
	@RestClient
	private TwitchAuthApi twitchAuthApi;
	
	@Inject
	@RestClient
	private IgdbApi igdbApi;
	
	@Inject
	private ClientID.Repository clientIdRepository;
	
	@Inject
	private IgdbCache detailsCache;
	
	@Inject
	private GameDetails.Repository gameDetailsRepository;
	
	@Path("@add")
	@GET
	@View("source/igdb/detailsAdd.jsp")
	public void addDetails(@NotEmpty @QueryParam("game") String gameId, @QueryParam("title") String title) {
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
		List<SearchResult> searchResults = igdbApi.search(twitchId.getClientId(), "Bearer " + token, "fields *; search \"" + search + "\";");
		
		models.put("searchResults", searchResults);
		models.put("gameId", gameId);
		models.put("search", search);
	}
	
	@Path("@addSpecific")
	@GET
	@View("source/igdb/detailsAddSpecific.jsp")
	public void addDetailsSpecific(@NotEmpty @QueryParam("game") String gameId, @QueryParam("resultId") int resultId) {
		Game game = gameRepository.findById(gameId)
			.orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("Unable to find game for ID {0}", gameId)));
		
		ClientID twitchId = clientIdRepository.findByServiceType("twitch")
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Could not find Twitch Client ID"));
		
		OAuthResponse authResponse = twitchAuthApi.getToken(twitchId.getClientId(), twitchId.getClientSecret(), TwitchAuthApi.GrantType.client_credentials);
		
		String token = authResponse.accessToken();
		
		List<IgdbGame> igdbGames = igdbApi.listGames(twitchId.getClientId(), "Bearer " + token, "fields *; where id = " + resultId + ";");
		if(igdbGames.isEmpty()) {
			throw new NotFoundException(MessageFormat.format("Unable to find IGDB game for ID {0}", resultId));
		}
		
		IgdbGame igdbGame = igdbGames.getFirst();

		List<IgdbScreenshot> screenshots = igdbGame.screenshotIds().stream()
			.filter(Objects::nonNull)
			.map(id -> igdbApi.listScreenshots(twitchId.getClientId(), "Bearer " + token, "fields *; where id = " + id + ";"))
			.filter(shots -> !shots.isEmpty())
			.map(List::getFirst)
			.toList();
		
		detailsCache.put(resultId, igdbGame, screenshots);
		
		models.put("game", game);
		models.put("igdbGame", igdbGames.getFirst());
		models.put("igdbScreenshots", screenshots);
		models.put("resultId", Integer.toString(resultId));
	}
	
	@Path("@addSpecific")
	@POST
	public String saveDetailsSpecific(@NotEmpty @FormParam("game") String gameId, @FormParam("resultId") int resultId) {
		IgdbGame game = detailsCache.get(resultId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Could not find cached details for ID {0}", resultId)));
		List<IgdbScreenshot> screenshots = detailsCache.getScreenshots(resultId)
			.orElseGet(Collections::emptyList);
		
		GameDetails details = game.toGameDetails(gameId);
		
		// Download and attach any screenshots
		Collection<java.nio.file.Path> cleanup = new HashSet<>();
		try {
			screenshots.stream()
				.map(screenshot -> {
					URI uri = URI.create(String.format("https://images.igdb.com/igdb/image/upload/t_720p/%s.webp", screenshot.imageId()));
					java.nio.file.Path download = AppUtil.download(uri, null);
					details.attachments().add(EntityAttachment.of(download));
					details.screenshots().add(new ScreenshotInfo(download.getFileName().toString(), screenshot.height() == null ? 0 : screenshot.height(), screenshot.width() == null ? 0 : screenshot.width()));
					return download;
				})
				.forEach(cleanup::add);
			gameDetailsRepository.save(details);
		} finally {
			AppUtil.deleteAll(cleanup);
		}
		
		return "redirect:library/" + gameId;
	}
	
}
