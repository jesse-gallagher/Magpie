package event;

import api.gog.model.DownloadableFile;
import model.GameDownloadPlan;

public record DownloadEndEvent(GameDownloadPlan plan, Class<?> type, DownloadableFile file) {

}
