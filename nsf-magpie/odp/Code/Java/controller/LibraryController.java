package controller;

import java.util.Comparator;
import java.util.List;

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
	public enum GameTitleSorter implements Comparator<Game> {
		INSTANCE;

		@Override
		public int compare(Game a, Game b) {
			
			
			return fromRoman(a.title()).compareToIgnoreCase(fromRoman(b.title()));
		}
		
		private String fromRoman(String title) {
			// Ultima games are numbered as such, with the TM
			return title.replaceAll("I\u2122", "1")
					.replaceAll("II\u2122", "2")
					.replaceAll("III\u2122", "3")
					.replaceAll("IV\u2122", "4")
					.replaceAll("V\u2122", "5")
					.replaceAll("VI\u2122", "6")
					.replaceAll("VII\u2122", "7")
					.replaceAll("VIII\u2122", "8");
		}
	}
	
	@Inject
	private Models models;
	
	@Inject
	private Game.Repository gameRepository;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String list() {
		List<Game> games = gameRepository.list(Sort.asc("title"))
			.sorted(GameTitleSorter.INSTANCE)
			.toList();
		models.put("games", games);
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
