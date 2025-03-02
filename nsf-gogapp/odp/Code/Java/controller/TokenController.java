package controller;

import java.text.MessageFormat;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import api.gog.GogAccountApi;
import api.gog.GogAuthApi;
import api.gog.model.TokenResponse;
import api.gog.model.UserData;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import model.UserToken;

@Path("userTokens")
@Controller
public class TokenController {
	@Inject
	private Models models;
	
	@Inject
	@RestClient
	private GogAuthApi authApi;
	
	@Inject
	@RestClient
	private GogAccountApi accountApi;
	
	@Inject
	private UserToken.Repository tokenRepository;
	
	@Inject
	private UserToken.Repository userTokens;
	
	@GET
	public String list() {
		models.put("tokens", tokenRepository.list().toList());
		
		return "tokens/list.jsp";
	}
	
	@Path("@add")
	@POST
	public String addUserToken(@FormParam("code") String code) {
		TokenResponse response = authApi.getToken(
			GogAuthApi.DEFAULT_CLIENT_ID,
			GogAuthApi.DEFAULT_CLIENT_SECRET,
			GogAuthApi.GrantType.authorization_code,
			code,
			GogAuthApi.DEFAULT_REDIRECT_URI,
			null
		);
		
		UserToken token = new UserToken();
		
		token.setServiceType("gog");
		token.setUserId(response.userId());
		token.setAuthorizationCode(code);
		token.setAccessToken(response.accessToken());
		token.setRefreshToken(response.refreshToken());
		
		// Look up some extra informationn about the user
		UserData userData = accountApi.getUserData("Bearer " + token.getAccessToken());
		token.setUsername(userData.username());
		token.setEmail(userData.email());
		
		token = userTokens.save(token, true);
		
		return "redirect:userTokens/" + token.getDocumentId();
	}
	
	@Path("{tokenId}")
	@GET
	public String showUserToken(@PathParam("tokenId") String tokenId) {
		UserToken token = tokenRepository.findById(tokenId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Could not find token for ID {0}", tokenId)));
		
		models.put("token", token);
		
		return "tokens/token.jsp";
	}
}
