package model;

import java.util.Optional;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

/**
 * Contains information found by searching for a game, regardless of whether
 * or not is is downloaded.
 */
@Entity
public record GameMetadata(
	@Id String documentId,
	@Column int gameId,
	@Column String imageUrl
) {
	@RepositoryProvider("storage")
	public interface Repository extends DominoRepository<GameMetadata, String> {
		@ViewEntries("Game Metadata")
		Optional<GameMetadata> findByGameId(ViewQuery query);
	}
}
