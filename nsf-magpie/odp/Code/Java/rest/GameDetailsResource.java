package rest;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import model.GameDetails;
import util.AppUtil;

@Path("api/gamedetails")
public class GameDetailsResource {
	@Inject
	private GameDetails.Repository gameDetailsRepository;
	
	@Path("{id}/{fileName}")
	@GET
	public Response getAttachment(@Context Request request, @PathParam("id") String id, @PathParam("fileName") String fileName) throws IOException {
		GameDetails entity = gameDetailsRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Could not find game details for ID " + id));
		
		String expectedName = fileName.replace('+', ' ').toLowerCase();
		return AppUtil.serveAttachment(request, entity, entity.documentId(), entity.attachments(), expectedName);
	}
}
