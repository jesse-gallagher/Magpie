package event;

import api.DownloadableFile;
import model.GameDownloadPlan;

public record DownloadProgressEvent(GameDownloadPlan plan, DownloadableFile file, long downloaded) {

}
