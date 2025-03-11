package event;

import api.DownloadableFile;
import model.GameDownloadPlan;

public record DownloadStartEvent(GameDownloadPlan plan, Class<?> type, DownloadableFile file) {

}
