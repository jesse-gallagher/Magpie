<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<c:if test="${plan.state == 'Planned' or plan.state == 'InProgress'}">
	<script>
		function formatBytes(bytes) {
		    if (!bytes) return '0';
	
		    const k = 1024;
		    const sizes = ['B', 'K', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
	
		    const i = Math.floor(Math.log(bytes) / Math.log(k));
	
		    return `\${parseFloat((bytes / Math.pow(k, i)).toFixed(0))} \${sizes[i]}`;
		}
		
		let updateStatus = null;
		
		updateStatus = () => {
			const apiUrl = "${mvc.basePath}/api/gamedownload/${plan.documentId}/@status";
			fetch(apiUrl, { includeCredentials: true })
				.then((response) => response.json())
				.then((planStatus) => {
					if(planStatus.plan.state === "Planned" || planStatus.plan.state === "InProgress") {
						// Update the list
						
						document.getElementById("planState").innerText = planStatus.plan.state;

						const ul = document.getElementById("planDownloads");
						ul.innerHTML = "";
						planStatus.activeDownloads.forEach((download) => {
							const li = document.createElement("li");
							li.innerText = `\${download.file.name} (\${formatBytes(download.downloaded)}/\${formatBytes(download.totalSize)})`;
							ul.appendChild(li);
						});
						
						if(planStatus.plan.state === "InProgress") {
							// For now, progress is just based on count of downloads, not size
							const totalCount = planStatus.plan.installerUrls.length + planStatus.plan.extraUrls.length;
							const completeCount = planStatus.plan.installerIds.length + planStatus.plan.extraIds.length;
							
							if(totalCount > 0) {
								const progress = document.getElementById("planProgress");
								progress.value = (completeCount / totalCount) * 100;
								progress.innerText = ((completeCount / totalCount) * 100) + "";
							}
						}
						setTimeout(() => updateStatus(), 2500);
					} else {
						// Otherwise, reload the page to show the results or exception
						window.location = "${mvc.basePath}/library/" + encodeURIComponent(planStatus.plan.gameDocumentId);
					}
				});
			
		}
	
		setTimeout(() => updateStatus(), 2500);
	</script>
	</c:if>

	<h2><c:out value="${translation.gameDownloadPlan}"/></h2>
	
	
	<c:if test="${plan.state == 'Planned' or plan.state == 'InProgress'}">
		<progress id="planProgress" max="100"></progress>
		<ul id="planDownloads"></ul>
	</c:if>
	
	<dl>
		<dt><c:out value="${translation.state}"/></dt>
		<dd id="planState"><c:out value="${plan.state}"/></dd>
	
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