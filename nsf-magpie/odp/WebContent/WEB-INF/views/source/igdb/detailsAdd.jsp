<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<t:layout>
	<h1><c:out value="${translation.addGameDetails}" /></h1>
	
	<form method="GET" action="${mvc.basePath}/source/igdb/@add">
		<input type="hidden" name="game" value="${gameId}"/>
		<input type="search" name="title" value="${fn:escapeXml(search)}"/>
		<input type="submit" value="${fn:escapeXml(translation.search)}"/>
	</form>
	
	<ul>
	<c:forEach items="${searchResults}" var="searchResult">
		<li><a href="${mvc.basePath}/source/igdb/@addSpecific?game=${gameId}&amp;resultId=${searchResult.gameId}"><c:out value="${searchResult.name}"/></a></li>
	</c:forEach>
	</ul>
</t:layout>