package api.gog.model;

public record Product(
	int id,
	String title,
	String image,
	String url,
	String category,
	ReleaseDate releaseDate
) {}