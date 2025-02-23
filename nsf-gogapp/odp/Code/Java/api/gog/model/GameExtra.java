package api.gog.model;

public record GameExtra(
	String manualUrl,
	String name,
	String type,
	int info,
	String size
) {}