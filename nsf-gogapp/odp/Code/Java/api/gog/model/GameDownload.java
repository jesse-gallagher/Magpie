package api.gog.model;

import jakarta.json.bind.annotation.JsonbProperty;
import util.AppUtil;

public record GameDownload(
	String manualUrl,
	String name,
	String version,
	String date,
	String size
) implements DownloadableFile {
	@JsonbProperty("sizeBytes")
	@Override
	public long getSizeBytes() {
		return AppUtil.decodeSizeString(size);
	}

	@JsonbProperty("downloadUrl")
	@Override
	public String getDownloadUrl() {
		return manualUrl;
	}
}