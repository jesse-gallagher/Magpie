<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h2><c:out value="${translation.gogGame}"/></h2>
	
	<dl>
		<dt><c:out value="${translation.gameTitle}"/></dt>
		<dd><c:out value="${details.title}"/></dd>
		
		<dt><c:out value="${translation.downloads}"/></dt>
		<dd>
			<table>
				<thead>
					<tr>
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
						<td><c:out value="${entry.os}"/></td>
						<td><c:out value="${entry.language}"/></td>
						<td><c:out value="${entry.download.name}"/></td>
						<td><c:out value="${entry.download.version}"/></td>
						<td><c:out value="${entry.download.date}"/></td>
						<td><c:out value="${entry.download.size}"/></td>
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
						<th><c:out value="${translation.name}"/></th>
						<th><c:out value="${translation.type}"/></th>
						<th><c:out value="${translation.size}"/></th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${details.extras}" var="extra">
					<tr>
						<td><c:out value="${extra.name}"/></td>
						<td><c:out value="${extra.type}"/></td>
						<td><c:out value="${extra.size}"/></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</dd>
	</dl>
	
	<form method="POST" action="${mvc.basePath}/gog/game/@download">
		<input type="hidden" name="gameId" value="${gameId}"/>
		<input type="hidden" name="tokenId" value="${tokenId}"/>
		<input type="submit" value="Download Game Data"/>
	</form>
</t:layout>