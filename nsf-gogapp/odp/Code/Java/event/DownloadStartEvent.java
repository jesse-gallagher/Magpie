package event;

import api.gog.model.DownloadableFile;
import model.GameDownloadPlan;

public record DownloadStartEvent(GameDownloadPlan plan, Class<?> type, DownloadableFile file) {

}
