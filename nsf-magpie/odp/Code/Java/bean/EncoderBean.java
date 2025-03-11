package bean;

import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ibm.commons.util.StringUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

/**
 * This bean is intended to be a JSP utility bean for text encoding.
 */
@RequestScoped @Named("encoder")
public class EncoderBean {
	
	@Inject @Named("dominoSession")
	private Session session;
	
	/**
	 * URL-encodes the provided value, using {@link URLEncoder#encode(String, String)}.
	 *
	 * @param value the value to URL-encode
	 * @return the URL-encoded value
	 */
	public String urlEncode(final String value) {
		if(StringUtil.isEmpty(value)) {
			return StringUtil.EMPTY_STRING;
		} else {
			try {
				return URLEncoder.encode(value, "UTF-8");
			} catch(UnsupportedEncodingException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
	
	/**
	 * Converts a name to Domino-style "abbreviated" format.
	 * 
	 * @param name the name to convert, such as one in Domino canonical format
	 * @return an abbreviated form of the name
	 */
	public String abbreviateName(String name) throws NotesException {
		Name dominoName = session.createName(name);
		try {
			return dominoName.getAbbreviated();
		} finally {
			dominoName.recycle();
		}
	}
}