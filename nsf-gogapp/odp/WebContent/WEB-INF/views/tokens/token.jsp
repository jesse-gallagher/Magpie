<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h2><c:out value="${translation.userTokens}"/></h2>
	
	<fieldset>
		<legend><c:out value="${translation.addUserToken}"/></legend>
		
		<p><c:out value="${translation.addUserTokenDescHtml}" escapeXml="false"/></p>
		
		<form method="POST" action="${mvc.basePath}/getUserToken">
			<p><label><c:out value="${translation.userTokenCodePrompt}"/> <input type="text" name="code" /></label></p>
			<p><input type="submit" value="${fn:escapeXml(translation.userTokenAddSubmit)}" /></p>
		</form>
	</fieldset>
	
	<table>
	<thead>
		<tr>
			<th><c:out value="${translation.userId}"/></th>
			<th><c:out value="${translation.created}"/></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${tokens}" var="token">
		<tr>
			<td><c:out value="${token.userId}"/></td>
			<td><c:out value="${token.created}"/></td>
		</tr>
		</c:forEach>
	</tbody>
	</table>
	
	
</t:layout>