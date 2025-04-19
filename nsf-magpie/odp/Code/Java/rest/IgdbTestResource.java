package rest;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import api.igdb.v4.IgdbApi;
import api.twitch.TwitchAuthApi;
import api.twitch.model.OAuthResponse;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import model.ClientID;

@Path("api/igdb")
public class IgdbTestResource {
	@Inject
	@RestClient
	private TwitchAuthApi twitchAuthApi;
	
	@Inject
	@RestClient
	private IgdbApi idgbApi;
	
	@Inject
	private ClientID.Repository clientIdRepository;
	
	@Path("search")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Object search(@NotEmpty @QueryParam("title") String title) {
		ClientID twitchId = clientIdRepository.findByServiceType("twitch")
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Could not find Twitch Client ID"));
		
		OAuthResponse authResponse = twitchAuthApi.getToken(twitchId.getClientId(), twitchId.getClientSecret(), TwitchAuthApi.GrantType.client_credentials);
		
		String token = authResponse.accessToken();
		
		Object response = idgbApi.search(twitchId.getClientId(), "Bearer " + token, "fields *; search \"" + title + "\";");
		
		return response;
	}
}
