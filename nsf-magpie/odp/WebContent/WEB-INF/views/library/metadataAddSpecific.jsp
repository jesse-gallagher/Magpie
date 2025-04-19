<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h1><c:out value="${translation.metadata}" /></h1>
	
	<dl>
		<dt><c:out value="${translation.title}"/></dt>
		<dd><c:out value="${igdbGame.name}"/></dd>
		
		<dt><c:out value="${translation.summary}"/></dt>
		<dd><c:out value="${igdbGame.summary}"/></dd>
	</dl>
	
	<form method="POST" action="${mvc.basePath}/library/metadata/@addSpecific">
		<input type="hidden" name="game" value="${game.documentId}"/>
		<input type="hidden" name="resultId" value="${resultId}"/>
		<input type="submit" value="${fn:escapeXml(translation.saveDetails)}"/>
	</form>
</t:layout>