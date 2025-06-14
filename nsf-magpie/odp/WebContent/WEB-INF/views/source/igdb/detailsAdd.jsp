<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h1><c:out value="${translation.addGameMetadata}" /></h1>
	
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