package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.commons.util.StringUtil;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

public enum AppUtil {
	;
    public static final int DEFAULT_DOWNLOAD_BUFFER_SIZE = 16384;
	
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
			return switch(unitPart) {
				case "MB" -> (long)(num * 1024 * 1024);
				case "GB" -> (long)(num * 1024 * 1024 * 1024);
				case "TB" -> (long)(num * 1024 * 1024 * 1024 * 1024);
				default -> (long)num;
			};
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
	
	public static Path download(URI uri, String destFileName) {
		try(HttpClient http = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build()) {
			HttpRequest req = HttpRequest.newBuilder(uri)
				.GET()
				.build();
			
			HttpResponse<InputStream> resp = http.send(req, BodyHandlers.ofInputStream());
			String finalUri = resp.uri().toString();
			int slashIndex = finalUri.lastIndexOf('/');
			String fileName = destFileName;
			if(StringUtil.isEmpty(fileName)) {
				fileName = finalUri.substring(slashIndex+1)
					.replace('%', '_')
					.replace('/', '_')
					.replace('\\', '_');
			}
			
			java.nio.file.Path tempDir = Files.createTempDirectory(AppUtil.class.getName());
			java.nio.file.Path tempFile = tempDir.resolve(fileName);
			
			try(
				InputStream is = resp.body();
				OutputStream out = Files.newOutputStream(tempFile);
			) {
				long transferred = 0;
		        byte[] buffer = new byte[DEFAULT_DOWNLOAD_BUFFER_SIZE];
		        int read;
		        while ((read = is.read(buffer, 0, DEFAULT_DOWNLOAD_BUFFER_SIZE)) >= 0) {
		        	
		            out.write(buffer, 0, read);
		            if (transferred < Long.MAX_VALUE) {
		                try {
		                    transferred = Math.addExact(transferred, read);
		                } catch (ArithmeticException ignore) {
		                    transferred = Long.MAX_VALUE;
		                }
		            }
		        }
			}
			return tempFile;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void deleteAll(Collection<Path> paths) {
		paths.forEach(p -> {
			try {
				Files.deleteIfExists(p);
			} catch(IOException e) {
				// Ignore
			}
		});
	}
	
	public static Response serveAttachment(Request request, Object entity, String entityId, List<EntityAttachment> attachments, String expectedName) throws IOException {
		EntityAttachment att = attachments.stream()
        	.filter(a -> StringUtil.toString(a.getName()).toLowerCase().endsWith(expectedName))
        	.findFirst()
        	.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find {0} in {1} {2}", expectedName, entity.getClass().getSimpleName(), entityId)));

        EntityTag etag = new EntityTag(att.getETag());
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if(builder == null) {
            builder = Response.ok(att.getData(), att.getContentType())
                .tag(etag);
        }

        CacheControl cc = new CacheControl();
        cc.setMaxAge(5 * 24 * 60 * 60);

        return builder
            .cacheControl(cc)
            .lastModified(new Date(att.getLastModified()))
            .build();
	}
}
