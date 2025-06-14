package bean.igdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import api.igdb.v4.model.IgdbGame;
import api.igdb.v4.model.IgdbScreenshot;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IgdbCache {
	private Map<Integer, IgdbGame> cache = new HashMap<>();
	private Map<Integer, List<IgdbScreenshot>> screenshotCache = new HashMap<>();
	
	public void put(int resultId, IgdbGame details, List<IgdbScreenshot> screenshotUrls) {
		this.cache.put(resultId, details);
		this.screenshotCache.put(resultId, screenshotUrls);
	}
	
	public Optional<IgdbGame> get(int resultId) {
		return Optional.ofNullable(cache.get(resultId));
	}
	
	public Optional<List<IgdbScreenshot>> getScreenshots(int resultId) {
		return Optional.ofNullable(screenshotCache.get(resultId));
	}
}
