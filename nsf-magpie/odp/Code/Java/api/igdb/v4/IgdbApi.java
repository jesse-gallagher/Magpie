package api.igdb.v4;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.json.JsonArray;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

/**
 * @see <a href="https://api-docs.igdb.com">https://api-docs.igdb.com</a>
 */
@RegisterRestClient(baseUri = IgdbApi.BASE_URI)
public interface IgdbApi {
	String BASE_URI = "https://api.igdb.com/v4";
	
	@POST
	@Path("games")
	@Produces(MediaType.APPLICATION_JSON)
	JsonArray listGames(@HeaderParam("Client-ID") String clientId, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String body);

	@POST
	@Path("search")
	@Produces(MediaType.APPLICATION_JSON)
	JsonArray search(@HeaderParam("Client-ID") String clientId, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String body);
}
