<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<t:layout>
	<h2><c:out value="${translation.gameLibrary}"/></h2>
	
	<c:if test="${userTokens.anyExist}">
		<details class="expanding-form">
			<summary><c:out value="${translation.addFromGog}"/></summary>
			
			<form method="GET" action="${mvc.basePath}/source/gog/search">
				<fieldset>
					<label for="searchField"><c:out value="${translation.namePrompt}"/></label>
					<input type="text" name="search" required id="searchField"/>
					
					<label for="tokenIdField"><c:out value="${translation.userTokenPrompt}"/></label>
					<select name="tokenId" required>
					<c:forEach items="${userTokens.forType('gog')}" var="token">
						<option value="${token.documentId}"><c:out value="${token.username}"/></option>
					</c:forEach> 
					</select>
					<input class="button-primary" type="submit" value="${fn:escapeXml(translation.search)}" />
				</fieldset>
			</form>
		</details>
	</c:if>
	
	<section class="gallery">
	<c:forEach items="${games}" var="game">
		<article>
			<figure>
				<c:if test="${not empty game.imageFileName}">
					<a href="${mvc.basePath}/library/${game.documentId}">
						<img src="${mvc.basePath}/api/games/${game.documentId}/${game.imageFileName}" />
					</a>
				</c:if>
				<figcaption><a href="${mvc.basePath}/library/${game.documentId}"><c:out value="${game.title}"/></a></figcaption>
			</figure>
		</article>
	</c:forEach>
	</section>
</t:layout>