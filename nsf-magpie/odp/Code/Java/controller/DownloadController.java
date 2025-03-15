package controller;

import java.text.MessageFormat;

import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import model.GameDownloadPlan;

@Path("downloads")
@Controller
public class DownloadController {

	@Inject
	private Models models;
	
	@Inject
	private GameDownloadPlan.Repository gameDownloadPlanRepository;
	
	@Path("{planId}")
	@GET
	public String showDownloadPlan(@PathParam("planId") String planId) {
		GameDownloadPlan plan = gameDownloadPlanRepository.findById(planId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find download plan for ID {0}", planId)));
		
		models.put("plan", plan);
		
		return "downloads/downloadPlan.jsp";
	}
}
