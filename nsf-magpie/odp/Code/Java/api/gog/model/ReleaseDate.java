package api.gog.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public record ReleaseDate(String date, int timezoneType, String timezone) {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnn");
	
	public OffsetDateTime toOffsetDateTime() {
		ZoneId zoneId = ZoneId.of(timezone);
		LocalDateTime localDateTime = LocalDateTime.from(FORMATTER.parse(date));
		ZonedDateTime dt = ZonedDateTime.of(localDateTime, zoneId);
		return dt.toOffsetDateTime();
	}
}
