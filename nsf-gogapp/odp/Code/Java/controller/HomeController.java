package controller;

import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import api.gog.GogAccountApi;
import api.gog.GogAuthApi;
import api.gog.GogAuthApi.GrantType;
import api.gog.model.FilteredProducts;
import api.gog.model.TokenResponse;
import bean.UserTokenBean;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.GameMetadata;
import model.UserToken;

@Controller
@Path("/")
public class HomeController {
	@Inject
	private Models models;
	
	@Inject
	@RestClient
	private GogAuthApi authApi;
	
	@Inject
	@RestClient
	private GogAccountApi accountApi;
	
	@Inject
	private UserToken.Repository userTokens;
	
	@Inject
	private GameMetadata.Repository metadataRepository;
	
	@Inject
	private UserTokenBean userTokenBean;

	@Inject
	private HttpServletRequest request;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String get() {
		return "home.jsp";
	}
	
	@Path("getUserToken")
	@POST
	@Produces(MediaType.TEXT_HTML)
	public String showUserInfo(@FormParam("code") String code) {
		TokenResponse response = authApi.getToken(
			GogAuthApi.DEFAULT_CLIENT_ID,
			GogAuthApi.DEFAULT_CLIENT_SECRET,
			GrantType.authorization_code,
			code,
			GogAuthApi.DEFAULT_REDIRECT_URI,
			null
		);
		
		String userName = request.getRemoteUser();
		UserToken tokenEntity = userTokens.findByUserName(userName)
			.orElseGet(UserToken::new);
		
		tokenEntity.setUserName(userName);
		tokenEntity.setAuthorizationCode(code);
		tokenEntity.setAccessToken(response.accessToken());
		tokenEntity.setRefreshToken(response.refreshToken());
		
		userTokens.save(tokenEntity, true);
		
		models.put("userToken", tokenEntity.getAccessToken());
		
		return "getUserToken.jsp";
	}
	
	@Path("search")
	@POST
	@Produces(MediaType.TEXT_HTML)
	public String search(@FormParam("search") String search) {
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
		
		return "search.jsp";
	}
}
