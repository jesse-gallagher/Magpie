package api.igdb.v4.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;
import model.GameDetails;

public record IgdbGame(
	@JsonbProperty("age_ratings") List<Integer> ageRatingIds,
	@JsonbProperty("aggregated_rating") Double aggregatedRating,
	@JsonbProperty("aggregated_rating_count") Integer aggregatedRatingCount,
	@JsonbProperty("alternative_names") List<String> alternativeNames,
	@JsonbProperty("artworks") List<Integer> artworkIds,
	@JsonbProperty("bundles") List<Integer> bundleIds,
	@Deprecated @JsonbProperty("category") Integer categoryId,
	String checksum,
	@Deprecated @JsonbProperty("collection") Integer collectionId,
	@JsonbProperty("collections") List<Integer> collectionIds,
	@JsonbProperty("cover") Integer coverId,
	@JsonbProperty("created_at") Long created,
	@JsonbProperty("dlcs") List<Integer> dlcIds,
	@JsonbProperty("expanded_games") List<Integer> expandedGameIds,
	@JsonbProperty("expansions") List<Integer> expansionIds,
	@JsonbProperty("external_games") List<Integer> externalGameIds,
	@JsonbProperty("first_release_date") Long firstReleaseDate,
	@Deprecated Integer follows,
	@JsonbProperty("forks") List<Integer> forkIds,
	@JsonbProperty("franchise") Integer franchiseId,
	@JsonbProperty("franchises") List<Integer> franchiseIds,
	@JsonbProperty("game_engines") List<Integer> gameEngineIds,
	@JsonbProperty("game_localizations") List<Integer> gameLocalizationIds,
	@JsonbProperty("game_modes") List<Integer> gameModeIds,
	@JsonbProperty("game_status") Integer gameStatusId,
	@JsonbProperty("game_type") Integer gameTypeId,
	@JsonbProperty("genres") List<Integer> genreIds,
	Integer hypes,
	@JsonbProperty("involved_companies") List<Integer> involvedCompanyIds,
	@JsonbProperty("keywords") List<Integer> keywordIds,
	@JsonbProperty("language_supports") List<Integer> languageSupportIds,
	@JsonbProperty("multiplayer_modes") List<Integer> multiplayerModeIds,
	String name,
	@JsonbProperty("parent_game") Integer parentGameId,
	@JsonbProperty("platforms") List<Integer> platformIds,
	@JsonbProperty("player_perspectives") List<Integer> playerPerspectiveIds,
	@JsonbProperty("ports") List<Integer> portIds,
	Double rating,
	@JsonbProperty("rating_count") Integer ratingCount,
	@JsonbProperty("release_dates") List<Integer> releaseDateIds,
	@JsonbProperty("ramakes") List<Integer> remakes,
	@JsonbProperty("remasters") List<Integer> remasters,
	@JsonbProperty("screenshots") List<Integer> screenshotIds,
	@JsonbProperty("similar_games") List<Integer> similarGameIds,
	String slug,
	@JsonbProperty("standalone_expansions") List<Integer> standaloneExpansionIds,
	@Deprecated String status,
	String storyline,
	String summary,
	@JsonbProperty("tags") List<Integer> tagIds,
	@JsonbProperty("themes") List<Integer> themeIds,
	@JsonbProperty("total_rating") Double totalRating,
	@JsonbProperty("total_rating_count") Integer totalRatingCount,
	@JsonbProperty("updatedAt") Long updatedAt,
	String url,
	@JsonbProperty("version_parent") Integer versionParentId,
	@JsonbProperty("version_title") String versionTitle,
	@JsonbProperty("videos") List<Integer> gameVideoIds,
	@JsonbProperty("websites") List<Integer> websiteIds
) {
	
	public GameDetails toGameDetails(String gameId) {
		return new GameDetails(null, "igdb", gameId, url, summary, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}
}
