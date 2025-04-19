package api.twitch.model;

import jakarta.json.bind.annotation.JsonbProperty;

public record OAuthResponse(
	@JsonbProperty("access_token") String accessToken,
	@JsonbProperty("expires_in") int expiresIn,
	@JsonbProperty("token_type") String tokenType
) {

}
