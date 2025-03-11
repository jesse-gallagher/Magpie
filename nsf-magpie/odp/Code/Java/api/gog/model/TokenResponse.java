package api.gog.model;

import jakarta.json.bind.annotation.JsonbProperty;

public record TokenResponse(
	@JsonbProperty("expires_in") int expiresIn,
	String scope,
	@JsonbProperty("token_type") String tokenType,
	@JsonbProperty("user_id") String userId,
	@JsonbProperty("refresh_token") String refreshToken,
	@JsonbProperty("session_id") String sessionId,
	@JsonbProperty("access_token") String accessToken
) { }
