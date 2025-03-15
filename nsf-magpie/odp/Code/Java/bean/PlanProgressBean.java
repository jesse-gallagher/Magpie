package bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import api.DownloadableFile;
import event.DownloadEndEvent;
import event.DownloadProgressEvent;
import event.DownloadStartEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import util.AppUtil;

@ApplicationScoped
public class PlanProgressBean {
	public class DownloadProgress {
		private final DownloadableFile file;
		private long totalSize;
		private long downloaded = 0;
		
		public DownloadProgress(DownloadableFile file, long totalSize) {
			this.file = file;
			this.totalSize = totalSize;
		}
		
		public DownloadableFile getFile() {
			return file;
		}
		
		public long getTotalSize() {
			return totalSize;
		}
		
		public void setTotalSize(long totalSize) {
			this.totalSize = totalSize;
		}
		
		public long getDownloaded() {
			return downloaded;
		}
		public void setDownloaded(long downloaded) {
			this.downloaded = downloaded;
		}
	}
	
	private Map<String, List<DownloadProgress>> activeDownloads;
	
	@PostConstruct
	public void init() {
		this.activeDownloads = new ConcurrentHashMap<>();
	}
	
	public void processDownloadStart(@Observes DownloadStartEvent event) {
		List<DownloadProgress> files = getActiveDownloads(event.plan().getDocumentId());
		files.add(new DownloadProgress(event.file(), event.file().getSizeBytes()));
	}
	
	public void processDownloadEnd(@Observes DownloadEndEvent event) {
		List<DownloadProgress> files = getActiveDownloads(event.plan().getDocumentId());
		files.removeIf(prog -> prog.getFile().equals(event.file()));
	}
	
	public void processDownloadProgress(@Observes DownloadProgressEvent event) {
		List<DownloadProgress> files = getActiveDownloads(event.plan().getDocumentId());
		files.stream()
			.filter(prog -> prog.getFile().equals(event.file()))
			.findFirst()
			.ifPresent(prog -> {
				prog.setDownloaded(event.downloaded());
				if(event.total() > 0) {
					prog.setTotalSize(event.total());
				}
			});
	}
	
	public List<DownloadProgress> getActiveDownloads(String planId) {
		return AppUtil.computeIfAbsent(this.activeDownloads, planId, key -> Collections.synchronizedList(new ArrayList<>()));
	}
}
