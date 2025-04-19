package bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import model.GameDetails;

/**
 * Houses game details in memory before they're committed to the DB
 */
@ApplicationScoped
public class GameDetailsCache {
	private Map<String, GameDetails> cache = new HashMap<>();
	
	public void put(String key, GameDetails details) {
		this.cache.put(key, details);
	}
	
	public Optional<GameDetails> get(String key) {
		return Optional.ofNullable(cache.get(key));
	}
}
