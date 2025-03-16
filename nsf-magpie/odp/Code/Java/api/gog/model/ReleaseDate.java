package api.gog.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public record ReleaseDate(String date, int timezoneType, String timezone) {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnn");
	private static final String NULL_TIME = "-0001-11-30 00:00:00.000000";
	
	public OffsetDateTime toOffsetDateTime() {
		// Some GOG games come back with an invalid date like above, so punt on them
		if(NULL_TIME.equals(date)) {
			return OffsetDateTime.now();
		}
		
		ZoneId zoneId = ZoneId.of(timezone);
		LocalDateTime localDateTime = LocalDateTime.from(FORMATTER.parse(date));
		ZonedDateTime dt = ZonedDateTime.of(localDateTime, zoneId);
		return dt.toOffsetDateTime();
	}
}
