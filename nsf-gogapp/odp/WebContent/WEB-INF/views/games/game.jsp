<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h2>Game</h2>
	
	<figure class="game-logo">
		<c:if test="${not empty game.imageFileName}">
			<img src="${mvc.basePath}/api/games/${game.documentId}/${game.imageFileName}" />
		</c:if>
	</figure>
	
	<dl>
		<dt>Title</dt>
		<dd><c:out value="${game.title}"/></dd>
		
		<dt>Installers</dt>
		<dd>
			<table>
			<thead>
				<tr>
					<th>Name</th>
					<th>OS</th>
					<th>Language</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${game.getInstallers()}" var="installer">
				<tr>
					<td><c:out value="${installer.name}"/></td>
					<td><c:out value="${installer.os}"/></td>
					<td><c:out value="${installer.language}"/></td>
					<td>
						<c:forEach items="${installer.attachments}" var="att">
							<a href="${mvc.basePath}/api/installers/${installer.documentId}/${encoder.urlEncode(att.name)}">Download</a>
						</c:forEach>
					</td>
				</tr>
				</c:forEach>
			</tbody>
			</table>
		</dd>
		
		<dt>Extras</dt>
		<dd>
			<table>
			<thead>
				<tr>
					<th>Name</th>
					<th>Type</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${game.getGameExtras()}" var="extra">
				<tr>
					<td><c:out value="${extra.name}"/></td>
					<td><c:out value="${extra.type}"/></td>
					<td>
						<c:forEach items="${extra.attachments}" var="att">
							<a href="${mvc.basePath}/api/extras/${extra.documentId}/${encoder.urlEncode(att.name)}">Download</a>
						</c:forEach>
					</td>
				</tr>
				</c:forEach>
			</tbody>
			</table>
		</dd>
	</dl>
</t:layout>