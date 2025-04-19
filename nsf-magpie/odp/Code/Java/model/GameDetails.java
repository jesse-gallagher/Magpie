package model;

import java.util.stream.Stream;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public record GameDetails(
	@Id String documentId,
	@Column String source,
	@Column String gameId,
	@Column String url,
	@Column String summary
) {
	public interface Repository extends DominoRepository<GameDetails, String> {
		Stream<GameDetails> findByGameId(String gameId);
	}
}
