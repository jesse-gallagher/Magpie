package bean;

import org.eclipse.jnosql.mapping.DatabaseType;
import org.openntf.xsp.jakarta.nosql.communication.driver.DominoDocumentManager;
import org.openntf.xsp.jakarta.nosql.communication.driver.lsxbe.impl.DefaultDominoDocumentCollectionManager;
import org.openntf.xsp.jakartaee.module.ComponentModuleLocator;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@Dependent
public class AppDatabasesBean {
	@Produces
	@org.eclipse.jnosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "storage")
	public DominoDocumentManager getStorageManager() {
		return new DefaultDominoDocumentCollectionManager(
			() -> ComponentModuleLocator.getDefault().flatMap(ComponentModuleLocator::getUserDatabase).get(),
			() -> ComponentModuleLocator.getDefault().flatMap(ComponentModuleLocator::getSessionAsSigner).get()
		);
	}
}
