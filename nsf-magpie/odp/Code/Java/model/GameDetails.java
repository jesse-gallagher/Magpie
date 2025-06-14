package model;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.openntf.xsp.jakarta.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ItemStorage;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public record GameDetails(
	@Id String documentId,
	@Column String source,
	@Column String gameId,
	@Column String url,
	@Column String summary,
	@Column @ItemStorage(type = ItemStorage.Type.JSON) List<ImageInfo> screenshots,
	@Column @ItemStorage(type = ItemStorage.Type.JSON) List<ImageInfo> artworks,
	@Column(DominoConstants.FIELD_ATTACHMENTS) List<EntityAttachment> attachments
) {
	public interface Repository extends DominoRepository<GameDetails, String> {
		Stream<GameDetails> findByGameId(String gameId);
	}
	
	public record ImageInfo(String fileName, int height, int width) {}
	
	
}
