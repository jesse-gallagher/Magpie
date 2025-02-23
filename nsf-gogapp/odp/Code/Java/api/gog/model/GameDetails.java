package api.gog.model;

import java.util.List;

import jakarta.json.JsonArray;

public record GameDetails(
	String title,
	String backgroundImage,
	JsonArray downloads,
	List<GameExtra> extras
) {}