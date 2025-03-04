package api;

public interface DownloadableFile {
	String name();
	
	long getSizeBytes();
	
	String getDownloadUrl();
}
