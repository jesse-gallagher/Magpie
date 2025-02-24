<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h2><c:out value="${translation.gameDownloadPlan}"/></h2>
	
	<dl>
		<dt><c:out value="${translation.state}"/></dt>
		<dd><c:out value="${plan.state}"/></dd>
	
		<dt><c:out value="${translation.gameId}"/></dt>
		<dd><c:out value="${plan.gameId}"/></dd>
		
		<dt><c:out value="${translation.gameTitle}"/></dt>
		<dd><c:out value="${plan.game.title}"/></dd>
		
		<dt><c:out value="${translation.installers}"/></dt>
		<dd>
			<table>
			<thead>
				<tr>
					<th><c:out value="${translation.name}"/></th>
					<th><c:out value="${translation.operatingSystem}"/></th>
					<th><c:out value="${translation.language}"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${plan.installers}" var="installer">
				<tr>
					<td><c:out value="${installer.name}"/></td>
					<td><c:out value="${installer.os}"/></td>
					<td><c:out value="${installer.language}"/></td>
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
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${plan.extras}" var="extra">
					<tr>
						<td><c:out value="${extra.name}"/></td>
						<td><c:out value="${extra.type}"/></td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</dd>
	</dl>
</t:layout>