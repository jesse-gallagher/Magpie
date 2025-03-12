package util;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum AppUtil {
	;
	
	private static final Pattern SIZE_PATTERN = Pattern.compile("^(\\d+(\\.\\d+)?) (\\w\\w)$");
	
	/**
	 * Decodes a GOG-style English size string like "1 MB" or "0.9 GB" into bytes.
	 * 
	 * @param sizeString the English formatted size value
	 * @return the represented size in bytes
	 */
	public static long decodeSizeString(String sizeString) {
		Matcher matcher = SIZE_PATTERN.matcher(sizeString);
		if(matcher.matches()) {
			String numPart = matcher.group(1);
			String unitPart = matcher.group(3);
			
			double num = Double.parseDouble(numPart);
			switch(unitPart) {
			case "MB":
				return (long)(num * 1024 * 1024);
			case "GB":
				return (long)(num * 1024 * 1024 * 1024);
			case "TB":
				return (long)(num * 1024 * 1024 * 1024 * 1024);
			default:
				return (long)num;
			}
		}
		
		return 0;
	}
	

	public static <S, T> T computeIfAbsent(final Map<S, T> map, final S key, final Function<S, T> sup) {
		synchronized(map) {
			T result;
			if(!map.containsKey(key)) {
				result = sup.apply(key);
				map.put(key, result);
			} else {
				result = map.get(key);
			}
			return result;
		}
	}
	
	/**
	 * Translates the provided game title to a default expecting sorting
	 * title.
	 * 
	 * @param title the actual game title
	 * @return a default sorting title
	 */
	public static String toSortingTitle(String title) {
		// Ultima games are numbered as such, with the TM
		return title.replace(" I\u2122", " 1")
			.replace(" II\u2122", " 2")
			.replace(" III\u2122", " 3")
			.replace(" IV\u2122", " 4")
			.replace(" V\u2122", " 5")
			.replace(" VI\u2122", " 6")
			.replace(" VII\u2122", " 7")
			.replace(" VIII\u2122", " 8");
	}
}
