<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<t:layout>
	<h2><c:out value="${translation.gogGame}"/></h2>
	
	<form method="POST" action="${mvc.basePath}/source/gog/game/@download">
	
		<dl>
			<dt><c:out value="${translation.gameTitle}"/></dt>
			<dd><c:out value="${details.title}"/></dd>
			
			<dt><c:out value="${translation.releaseDate}"/></dt>
			<dd><c:out value="${empty metadata or empty metadata.releaseDate ? '' : metadata.releaseDate}"/></dd>
			
			<c:if test="${not empty details.cdKey}">
			<dt><c:out value="${translation.cdKey}"/></dt>
			<dd><c:out value="${details.cdKey}" escapeXml="false"/></dd>
			</c:if>
			
			<dt><c:out value="${translation.downloads}"/></dt>
			<dd>
				<table>
					<thead>
						<tr>
							<th class="checkbox-col"></th>
							<th><c:out value="${translation.operatingSystem}"/></th>
							<th><c:out value="${translation.language}"/></th>
							<th><c:out value="${translation.name}"/></th>
							<th><c:out value="${translation.version}"/></th>
							<th><c:out value="${translation.uploadDate}"/></th>
							<th><c:out value="${translation.size}"/></th>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="${details.getParsedDownloads()}" var="entry">
						<tr>
							<td class="checkbox-col">
								<c:if test="${not downloadedUrls.contains(entry.download.manualUrl)}">
								<input type="checkbox" name="downloadUrl" value="${fn:escapeXml(entry.download.manualUrl)}" checked />
								</c:if>
							</td>
							<td><c:out value="${entry.os}"/></td>
							<td><c:out value="${entry.language}"/></td>
							<td><c:out value="${entry.download.name}"/></td>
							<td><c:out value="${entry.download.version}"/></td>
							<td><c:out value="${entry.download.date}"/></td>
							<td><c:out value="${messages.formatFileSize(entry.download.getSizeBytes())}"/></td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</dd>
			
			<dt><c:out value="${translation.extras}"/></dt>
			<dd>
				<table>
					<thead>
						<tr>
							<th class="checkbox-col"></th>
							<th><c:out value="${translation.name}"/></th>
							<th><c:out value="${translation.type}"/></th>
							<th><c:out value="${translation.size}"/></th>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="${details.extras}" var="extra">
						<tr>
							<td class="checkbox-col">
								<c:if test="${not downloadedUrls.contains(extra.manualUrl)}">
								<input type="checkbox" name="extraUrl" value="${fn:escapeXml(extra.manualUrl)}" checked />
								</c:if>
							</td>
							<td><c:out value="${extra.name}"/></td>
							<td><c:out value="${extra.type}"/></td>
							<td><c:out value="${messages.formatFileSize(extra.getSizeBytes())}"/></td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</dd>
		</dl>
	
		<input type="hidden" name="gameId" value="${gameId}"/>
		<input type="hidden" name="tokenId" value="${tokenId}"/>
		<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
		<input type="submit" value="Download Game Data"/>
	</form>
</t:layout>