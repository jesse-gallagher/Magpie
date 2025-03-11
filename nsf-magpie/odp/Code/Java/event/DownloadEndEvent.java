package event;

import api.DownloadableFile;
import model.GameDownloadPlan;

public record DownloadEndEvent(GameDownloadPlan plan, Class<?> type, DownloadableFile file) {

}
