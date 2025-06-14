<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<t:layout>
	<h1><c:out value="${translation.gameDetails}" /></h1>
	
	<dl>
		<dt><c:out value="${translation.title}"/></dt>
		<dd><c:out value="${igdbGame.name}"/></dd>
		
		<dt><c:out value="${translation.summary}"/></dt>
		<dd><c:out value="${igdbGame.summary}"/></dd>
	</dl>
	
	<section class="gallery">
		<c:forEach items="${igdbScreenshots}" var="screenshot">
			<figure>
				<img src="//images.igdb.com/igdb/image/upload/t_720p/${screenshot.imageId}.webp" width="${screenshot.width}" height="${screenshot.height}"/>
			</figure>
		</c:forEach>
	</section>
	
	<form method="POST" action="${mvc.basePath}/source/igdb/@addSpecific">
		<input type="hidden" name="game" value="${game.documentId}"/>
		<input type="hidden" name="resultId" value="${resultId}"/>
		<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
		<input type="submit" value="${fn:escapeXml(translation.saveDetails)}"/>
	</form>
</t:layout>