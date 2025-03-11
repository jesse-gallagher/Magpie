package controller;

import jakarta.data.Sort;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.Game;

@Controller
@Path("library")
public class LibraryController {
	@Inject
	private Models models;
	
	@Inject
	private Game.Repository gameRepository;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String list() {
		models.put("games", gameRepository.list(Sort.asc("title")).toList());
		return "games/library.jsp";
	}
	
	@Path("{id}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String showGame(@PathParam("id") String id) {
		Game entity = gameRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Could not find game for ID " + id));
		
		models.put("game", entity);
		
		return "games/game.jsp";
	}
}
