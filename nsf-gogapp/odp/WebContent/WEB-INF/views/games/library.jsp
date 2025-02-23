<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h2>Game Library</h2>
	
	<ul class="product-list">
	<c:forEach items="${games}" var="game">
		<li>
			<figure>
				<c:if test="${not empty game.imageFileName}">
					<img src="${mvc.basePath}/api/games/${game.documentId}/${game.imageFileName}" />
				</c:if>
				<figcaption><a href="${mvc.basePath}/library/${game.documentId}"><c:out value="${game.title}"/></a></figcaption>
			</figure>
		</li>
	</c:forEach>
	</ul>
</t:layout>