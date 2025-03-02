package bean;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@RequestScoped
public class TranslationBean {
	@Inject
	HttpServletRequest request;

	@Produces @Named("translation")
	public ResourceBundle getTranslation() {
		// preload request won't have a locale
		Locale locale = request == null || request.getLocale() == null ? Locale.getDefault() : request.getLocale();
		return ResourceBundle.getBundle("translation", locale); //$NON-NLS-1$
	}

	@RequestScoped
	@Named("messages")
	public static class Messages {
		private static final ThreadLocal<NumberFormat> SIZE_FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("#,##0.#"));
		
		@Inject
		HttpServletRequest request;
		
		@Inject
		@Named("translation")
		private ResourceBundle translation;

		public String format(final String key, final Object... params) {
			String message = translation.getString(key);
			return MessageFormat.format(message, params);
		}
		
		public String formatStatic(String message, Object... params) {
			return MessageFormat.format(message, params);
		}
		
		/**
		 * Formats the keyed message from the translation bundle with the provided
		 * parameters if they key exists. If it doesn't, this returns the key
		 * itself.
		 * 
		 * @param key the key to look up in the translation bundle
		 * @param params the parameters to use when formatting
		 * @return a formatted message if the key has a translation, or the key
		 *         itself otherwise
		 */
		public String softFormat(final String key, final Object... params) {
			if(translation.containsKey(key)) {
				String message = translation.getString(key);
				return MessageFormat.format(message, params);
			} else {
				return key;
			}
		}

		public String getMonth(final int index) {
			return DateFormatSymbols.getInstance(request.getLocale()).getMonths()[index];
		}
		
		// h/t https://stackoverflow.com/a/5599842
		public String formatFileSize(long size) {
			if(size <= 0) return "0";
		    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB", "PB", "EB" };
		    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		    return SIZE_FORMAT.get().format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
		}
	}
}