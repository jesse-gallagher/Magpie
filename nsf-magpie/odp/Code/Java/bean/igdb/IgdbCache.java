package bean.igdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import api.igdb.v4.model.IgdbGame;
import api.igdb.v4.model.IgdbImage;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IgdbCache {
	public record CacheEntry(IgdbGame game, List<IgdbImage> screenshots, List<IgdbImage> artworks) {}
	
	private Map<Integer, CacheEntry> cache = new HashMap<>();
	
	public void put(int resultId, IgdbGame details, List<IgdbImage> screenshots, List<IgdbImage> artworks) {
		this.cache.put(resultId, new CacheEntry(details, screenshots, artworks));
	}
	
	public Optional<CacheEntry> get(int resultId) {
		return Optional.ofNullable(cache.get(resultId));
	}
}
