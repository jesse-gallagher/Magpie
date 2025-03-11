package rest;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

import com.ibm.commons.util.StringUtil;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import model.GameExtra;

@Path("api/extras")
public class GameExtraResource {
	@Inject
	private GameExtra.Repository gameExtraRepository;
	
	@Path("{id}/{fileName}")
	@GET
	public Response getAttachment(@Context Request request, @PathParam("id") String id, @PathParam("fileName") String fileName) throws IOException {
		GameExtra entity = gameExtraRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Could not find game extra for ID " + id));
		
		String expectedName = fileName.replace('+', ' ').toLowerCase();
        EntityAttachment att = entity.attachments()
        	.stream()
        	.filter(a -> StringUtil.toString(a.getName()).toLowerCase().endsWith(expectedName))
        	.findFirst()
        	.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find {0} {1} in document {2}", entity.getClass().getSimpleName(), fileName, entity.documentId())));

        EntityTag etag = new EntityTag(att.getETag());
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if(builder == null) {
            builder = Response.ok(att.getData(), att.getContentType())
                .tag(etag);
        }

        CacheControl cc = new CacheControl();
        cc.setMaxAge(5 * 24 * 60 * 60);

        return builder
            .cacheControl(cc)
            .lastModified(new Date(att.getLastModified()))
            .build();
	}
}
