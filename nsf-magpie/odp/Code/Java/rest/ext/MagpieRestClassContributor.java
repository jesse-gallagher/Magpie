package rest.ext;

import java.util.Collection;
import java.util.Set;

import org.openntf.xsp.jakarta.rest.RestClassContributor;

public class MagpieRestClassContributor implements RestClassContributor {

	@Override
	public Collection<Class<?>> getClasses() {
		return Set.of(EntityAttachmentWriter.class);
	}

}
