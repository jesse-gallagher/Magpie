<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<t:layout>
	<h2><c:out value="${translation.userTokens}"/></h2>
	
	<details class="expanding-form">
		<summary><c:out value="${translation.addUserToken}"/></summary>
		
		<p><c:out value="${translation.addUserTokenDescHtml}" escapeXml="false"/></p>
		
		<form method="POST" action="${mvc.basePath}/userTokens/@add">
			<p><label><c:out value="${translation.userTokenCodePrompt}"/> <input type="text" name="code" /></label></p>
			<p><input type="submit" value="${fn:escapeXml(translation.userTokenAddSubmit)}" /></p>
		</form>
	</details>
	
	<table>
	<thead>
		<tr>
			<th><c:out value="${translation.serviceType}"/></th>
			<th><c:out value="${translation.username}"/></th>
			<th><c:out value="${translation.emailAddress}"/></th>
			<th><c:out value="${translation.userId}"/></th>
			<th><c:out value="${translation.created}"/></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${tokens}" var="token">
		<tr>
			<td><c:out value="${token.serviceType}"/></td>
			<td><a href="${mvc.basePath}/userTokens/${encoder.urlEncode(token.documentId)}"><c:out value="${token.username}"/></a></td>
			<td><c:out value="${token.email}"/></td>
			<td><c:out value="${token.userId}"/></td>
			<td><c:out value="${token.created}"/></td>
		</tr>
		</c:forEach>
	</tbody>
	</table>
	
	
</t:layout>