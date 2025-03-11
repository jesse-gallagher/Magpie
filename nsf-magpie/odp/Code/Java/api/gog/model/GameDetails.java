package api.gog.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public record GameDetails(
	String title,
	String backgroundImage,
	String cdKey,
	String changelog,
	JsonArray downloads,
	List<GameExtra> extras,
	long releaseTimestamp,
	List<GameDetails> dlcs
) {
	
	public record DownloadEntry(String language, String os, GameDownload download) {}
	
	/**
	 * Parses the download list into a map of language -> OS -> downloads
	 * 
	 * @return a {@link Map} of game download information
	 */
	public List<DownloadEntry> getParsedDownloads() {
		Jsonb jsonb = JsonbBuilder.create();
		List<DownloadEntry> result = new ArrayList<>();
		
		String language = null;
		for(JsonValue value : downloads) {
			if(value.getValueType() == ValueType.ARRAY) {
				for(JsonValue innerValue : value.asJsonArray()) {
					if(innerValue.getValueType() == ValueType.STRING) {
						language = ((JsonString)innerValue).getString();
					} else if(innerValue.getValueType() == ValueType.OBJECT) {
						// Then it's a Map of OS -> Download[]
						JsonObject obj = innerValue.asJsonObject();
						for(Map.Entry<String, JsonValue> entry : obj.entrySet()) {
							String os = entry.getKey();
							for(JsonValue downloadValue : entry.getValue().asJsonArray()) {
								GameDownload download = jsonb.fromJson(downloadValue.asJsonObject().toString(), GameDownload.class);
								
								result.add(new DownloadEntry(language, os, download));
							}
						}
					}
				}
			}
		}
		
		return result;
	}
}