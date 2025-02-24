<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h2>Game</h2>
	
	<dl>
		<dt>Title</dt>
		<dd><c:out value="${details.title}"/></dd>
		
		<dt>Downloads</dt>
		<dd><c:out value="${details.downloads}"/></dd>
		
		<dt>Extras</dt>
		<dd>
			<ul>
				<c:forEach items="${details.extras}" var="extra">
					<li>
						<a href="https://gog.com${extra.manualUrl}"><c:out value="${extra.name}"/></a>
					</li>
				</c:forEach>
			</ul>
		</dd>
	</dl>
	
	<form method="POST" action="${mvc.basePath}/gog/game/@download">
		<input type="hidden" name="gameId" value="${gameId}"/>
		<input type="hidden" name="tokenId" value="${tokenId}"/>
		<input type="submit" value="Download Game Data"/>
	</form>
</t:layout>