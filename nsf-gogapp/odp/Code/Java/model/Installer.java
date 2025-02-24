package model;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.openntf.xsp.jakarta.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public record Installer(
	@Id String documentId,
	@Column(DominoConstants.FIELD_PARENTUNID) String parentDocumentId,
	@Column String name,
	@Column String language,
	@Column String os,
	@Column String url,
	@Column(DominoConstants.FIELD_ATTACHMENTS) List<EntityAttachment> attachments
) {

	@RepositoryProvider("storage")
	public interface Repository extends DominoRepository<Installer, String> {
		@ViewEntries("Installers")
		Stream<Installer> findByParentDocumentId(ViewQuery query);
	}
}