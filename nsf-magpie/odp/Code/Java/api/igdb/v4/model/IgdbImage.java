package api.igdb.v4.model;

import jakarta.json.bind.annotation.JsonbProperty;

public record IgdbImage(
	@JsonbProperty("alpha_channel") boolean alphaChannel,
	boolean animated,
	String checksum,
	@JsonbProperty("game") Integer gameId,
	Integer height,
	@JsonbProperty("image_id") String imageId,
	String url,
	Integer width
) { }