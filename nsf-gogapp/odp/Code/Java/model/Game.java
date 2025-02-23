package model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.openntf.xsp.jakarta.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import jakarta.data.Sort;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public record Game(
	@Id String documentId,
	@Column String title,
	@Column int id,
	@Column String imageFileName,
	@Column(DominoConstants.FIELD_ATTACHMENTS) List<EntityAttachment> attachments
) {
	@RepositoryProvider("storage")
	public interface Repository extends DominoRepository<Game, String> {
		Optional<Game> findByTitle(String title);
		
		@ViewEntries("Games")
		Stream<Game> list(Sort<Game> sort);
	}
	
	public List<Installer> getInstallers() {
		Installer.Repository repo = CDI.current().select(Installer.Repository.class).get();
		return repo.findByParentDocumentId(ViewQuery.query().category(documentId)).toList();
	}
	
	public List<GameExtra> getGameExtras() {
		GameExtra.Repository repo = CDI.current().select(GameExtra.Repository.class).get();
		return repo.findByParentDocumentId(ViewQuery.query().category(documentId)).toList();
	}
}