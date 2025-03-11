package model;

import java.time.LocalDate;
import java.util.Optional;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import api.gog.model.Product;
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
	@Column String imageUrl,
	@Column String category,
	@Column LocalDate releaseDate
) {
	@RepositoryProvider("storage")
	public interface Repository extends DominoRepository<GameMetadata, String> {
		@ViewEntries("Game Metadata")
		Optional<GameMetadata> findByGameId(ViewQuery query);
	}
	
	public static GameMetadata forProduct(Product product) {
		LocalDate releaseDate = null;
		if(product.releaseDate() != null) {
			releaseDate = product.releaseDate().toOffsetDateTime().toLocalDate();
		}
		
		return new GameMetadata(null, product.id(), product.image(), product.category(), releaseDate);
	}
}
