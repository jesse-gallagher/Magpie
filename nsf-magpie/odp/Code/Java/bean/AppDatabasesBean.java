package bean;

import org.eclipse.jnosql.mapping.DatabaseType;
import org.openntf.xsp.jakarta.nosql.communication.driver.DominoDocumentManager;
import org.openntf.xsp.jakarta.nosql.communication.driver.lsxbe.impl.DefaultDominoDocumentCollectionManager;

import com.ibm.domino.xsp.module.nsf.NotesContext;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@Dependent
public class AppDatabasesBean {
	@Produces
	@org.eclipse.jnosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "storage")
	public DominoDocumentManager getStorageManager() {
		return new DefaultDominoDocumentCollectionManager(() -> NotesContext.getCurrent().getCurrentDatabase(), () -> NotesContext.getCurrent().getSessionAsSigner());
	}
}
