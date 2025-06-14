<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<article class="game-display" style="background-image: url('${mvc.basePath}/api/games/${game.documentId}/${game.backgroundImageFileName}')">
	
		<header>
			<h2><c:out value="${game.title}"/></h2>
			
			<figure class="game-logo">
				<c:if test="${not empty game.imageFileName}">
					<img src="${mvc.basePath}/api/games/${game.documentId}/${game.imageFileName}" />
				</c:if>
			</figure>
		</header>
		
		<c:if test="${not empty details}">
			<p><c:out value="${details.summary}"/></p>
		</c:if>
		
		<p>
			<form method="GET" action="${mvc.basePath}/source/igdb/@add">
				<input type="hidden" name="game" value="${game.documentId}" />
				<input type="submit" value="${fn:escapeXml(translation.addGameMetadata)}" />
			</form>
		</p>
		
		<dl>
			<dt><c:out value="${translation.releaseDate}"/></dt>
			<dd><c:out value="${game.releaseDate}"/></dd>
			
			<c:if test="${not empty game.cdKey}">
			<dt><c:out value="${translation.cdKey}"/></dt>
			<dd><c:out value="${game.cdKey}" escapeXml="false"/></dd>
			</c:if>
		
			<dt><c:out value="${translation.installers}"/></dt>
			<dd>
				<table>
				<thead>
					<tr>
						<th><c:out value="${translation.name}"/></th>
						<th><c:out value="${translation.operatingSystem}"/></th>
						<th><c:out value="${translation.language}"/></th>
						<th><c:out value="${translation.version}"/></th>
						<th class="col-size"><c:out value="${translation.size}"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${game.getInstallers()}" var="installer">
					<c:forEach items="${installer.attachments}" var="att">
					<tr>
						<td>
							<a href="${mvc.basePath}/api/installers/${installer.documentId}/${encoder.urlEncode(att.name)}">
								<c:out value="${installer.name}"/>
							</a>
						</td>
						<td><c:out value="${installer.os}"/></td>
						<td><c:out value="${installer.language}"/></td>
						<td><c:out value="${installer.version}"/></td>
						<td class="col-size"><c:out value="${messages.formatFileSize(att.length)}"/></td>
					</tr>
					</c:forEach>
					</c:forEach>
				</tbody>
				</table>
			</dd>
			
			<dt><c:out value="${translation.extras}"/></dt>
			<dd>
				<table>
				<thead>
					<tr>
						<th><c:out value="${translation.name}"/></th>
						<th><c:out value="${translation.type}"/></th>
						<th class="col-size"><c:out value="${translation.size}"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${game.getGameExtras()}" var="extra">
					<c:forEach items="${extra.attachments}" var="att">
					<tr>
						<td>
							<a href="${mvc.basePath}/api/extras/${extra.documentId}/${encoder.urlEncode(att.name)}">
								<c:out value="${extra.name}"/>
							</a>
						</td>
						<td><c:out value="${extra.type}"/></td>
						<td class="col-size"><c:out value="${messages.formatFileSize(att.length)}"/></td>
					</tr>
					</c:forEach>
					</c:forEach>
				</tbody>
				</table>
			</dd>
		</dl>
	</article>
</t:layout>