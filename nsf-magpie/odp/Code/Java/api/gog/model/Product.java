package api.gog.model;

import java.time.LocalDate;

import model.GameMetadata;

public record Product(
	int id,
	String title,
	String image,
	String url,
	String category,
	ReleaseDate releaseDate
) {
	public GameMetadata toGameMetadata() {
		LocalDate releaseDate = null;
		if(releaseDate() != null) {
			releaseDate = releaseDate().toOffsetDateTime().toLocalDate();
		}
		
		return new GameMetadata(null, id(), image(), category(), releaseDate);
	}
}