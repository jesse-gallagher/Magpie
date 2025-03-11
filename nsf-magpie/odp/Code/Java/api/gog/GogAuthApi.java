package api.gog;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import api.gog.model.TokenResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * @see <a href="https://gogapidocs.readthedocs.io/en/latest/auth.html">https://gogapidocs.readthedocs.io/en/latest/auth.html</a>
 */
@RegisterRestClient(baseUri = GogAuthApi.BASE_URI)
public interface GogAuthApi {
	final String BASE_URI = "https://auth.gog.com";
	final String DEFAULT_CLIENT_ID = "46899977096215655";
	final String DEFAULT_CLIENT_SECRET = "9d85c43b1482497dbbce61f6e4aa173a433796eeae2ca8c5f6129f2dc4de46d9";
	final String DEFAULT_REDIRECT_URI = "https://embed.gog.com/on_login_success?origin=client";
	
	enum GrantType { authorization_code, refresh_token }
	
	@Path("/token")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	TokenResponse getToken(
		@QueryParam("client_id") @NotEmpty
		String clientId,
		@QueryParam("client_secret") @NotEmpty
		String clientSecret,
		@QueryParam("grant_type") @NotNull
		GrantType grantType,
		@QueryParam("code")
		String code,
		@QueryParam("redirect_uri")
		String redirectUri,
		@QueryParam("refresh_token")
		String refreshToken
	);
}
