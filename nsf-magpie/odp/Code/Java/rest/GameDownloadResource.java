package rest;

import java.util.List;

import bean.PlanProgressBean;
import bean.PlanProgressBean.DownloadProgress;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.GameDownloadPlan;

@Path("api/gamedownload")
public class GameDownloadResource {
	public record DownloadPlanStatus(GameDownloadPlan plan, List<DownloadProgress> activeDownloads) {}
	
	@Inject
	private GameDownloadPlan.Repository planRepository;
	
	@Inject
	private PlanProgressBean progressBean;
	
	@Path("{id}/@status")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DownloadPlanStatus getDownloadPlanStatus(@PathParam("id") String id) {
		GameDownloadPlan entity = planRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Could not find game plan for ID " + id));
		
		return new DownloadPlanStatus(entity, progressBean.getActiveDownloads(id));
	}
}