package api.twitch;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import api.twitch.model.OAuthResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@RegisterRestClient(baseUri = TwitchAuthApi.BASE_URI)
@Path("oauth2")
public interface TwitchAuthApi {
	String BASE_URI = "https://id.twitch.tv";
	
	enum GrantType {
		client_credentials
	}
	
	@POST
	@Path("token")
	OAuthResponse getToken(@QueryParam("client_id") String clientId, @QueryParam("client_secret") String clientSecret, @QueryParam("grant_type") GrantType grantType);
}
