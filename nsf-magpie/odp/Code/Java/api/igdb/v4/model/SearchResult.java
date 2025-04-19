package api.igdb.v4.model;

import jakarta.json.bind.annotation.JsonbProperty;

public record SearchResult(
	String name,
	@JsonbProperty("alternative_name") String alternativeName,
	@JsonbProperty("character") int characterId,
	String checksum,
	@JsonbProperty("collection") int collectionId,
	@JsonbProperty("company") int companyId,
	String description,
	@JsonbProperty("game") int gameId,
	@JsonbProperty("platform") int platformId,
	@JsonbProperty("published_at") long published_at
) {

}
