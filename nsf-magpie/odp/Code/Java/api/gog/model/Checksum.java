package api.gog.model;

import jakarta.json.bind.annotation.JsonbProperty;

public record Checksum(
	String cart,
	String games,
	String wishlist,
	@JsonbProperty("reviews_votes") String reviewsVotes,
	@JsonbProperty("games_rating") String gamesRating
) {}
