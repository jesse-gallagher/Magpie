package api.gog;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import api.gog.model.FilteredProducts;
import api.gog.model.GameDetails;
import api.gog.model.UserData;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * @see <a href="https://gogapidocs.readthedocs.io/en/latest/account.html">https://gogapidocs.readthedocs.io/en/latest/account.html</a>
 * @see <a href="https://gogapidocs.readthedocs.io/en/latest/listing.html">https://gogapidocs.readthedocs.io/en/latest/listing.html</a>
 */
@RegisterRestClient(baseUri = GogAccountApi.BASE_URI)
public interface GogAccountApi {
	final String BASE_URI = "https://embed.gog.com";
	final String TYPE_GAME = "1";
	
	@Path("/userData.json")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	UserData getUserData(@HeaderParam("Authorization") String authorization);
	
	@Path("/account/getFilteredProducts")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	FilteredProducts getFilteredProducts(@HeaderParam("Authorization") String authorization, @QueryParam("mediaType") String mediaType, @QueryParam("search") String search);
	
	@Path("/account/gameDetails/{game_id}.json")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	GameDetails getGameDetails(@HeaderParam("Authorization") String authorization, @PathParam("game_id") int gameId);
}
