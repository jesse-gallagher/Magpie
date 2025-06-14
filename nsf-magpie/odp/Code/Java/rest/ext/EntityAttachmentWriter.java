package rest.ext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;

@Produces("*/*")
public class EntityAttachmentWriter implements MessageBodyWriter<EntityAttachment> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return EntityAttachment.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(EntityAttachment t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		try(InputStream is = t.getData()) {
			is.transferTo(entityStream);
		}
	}

}
