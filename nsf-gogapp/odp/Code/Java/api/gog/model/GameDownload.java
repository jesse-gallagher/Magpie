package api.gog.model;

public record GameDownload(
	String manualUrl,
	String name,
	String version,
	String date,
	String size
) {}