package bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import api.gog.model.DownloadableFile;
import event.DownloadEndEvent;
import event.DownloadStartEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import util.AppUtil;

@ApplicationScoped
public class PlanProgressBean {
	
	private Map<String, List<DownloadableFile>> activeDownloads;
	
	@PostConstruct
	public void init() {
		this.activeDownloads = new ConcurrentHashMap<>();
	}
	
	public void processDownloadStart(@Observes DownloadStartEvent event) {
		List<DownloadableFile> files = getActiveDownloads(event.plan().getDocumentId());
		files.add(event.file());
	}
	
	public void processDownloadEnd(@Observes DownloadEndEvent event) {
		List<DownloadableFile> files = getActiveDownloads(event.plan().getDocumentId());
		files.remove(event.file());
	}
	
	public List<DownloadableFile> getActiveDownloads(String planId) {
		return AppUtil.computeIfAbsent(this.activeDownloads, planId, key -> Collections.synchronizedList(new ArrayList<>()));
	}
}
