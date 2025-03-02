package api.gog.model;

public interface DownloadableFile {
	String name();
	
	long getSizeBytes();
	
	String getDownloadUrl();
}
