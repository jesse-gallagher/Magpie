package bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import api.DownloadableFile;
import event.DownloadEndEvent;
import event.DownloadStartEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import util.AppUtil;

@ApplicationScoped
public class PlanProgressBean {
	public record DownloadProgress(DownloadableFile file) {}
	
	private Map<String, List<DownloadProgress>> activeDownloads;
	
	@PostConstruct
	public void init() {
		this.activeDownloads = new ConcurrentHashMap<>();
	}
	
	public void processDownloadStart(@Observes DownloadStartEvent event) {
		List<DownloadProgress> files = getActiveDownloads(event.plan().getDocumentId());
		files.add(new DownloadProgress(event.file()));
	}
	
	public void processDownloadEnd(@Observes DownloadEndEvent event) {
		List<DownloadProgress> files = getActiveDownloads(event.plan().getDocumentId());
		files.removeIf(prog -> prog.file().equals(event.file()));
	}
	
	public List<DownloadProgress> getActiveDownloads(String planId) {
		return AppUtil.computeIfAbsent(this.activeDownloads, planId, key -> Collections.synchronizedList(new ArrayList<>()));
	}
}
