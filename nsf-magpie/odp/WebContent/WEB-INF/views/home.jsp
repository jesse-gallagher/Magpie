<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="fn" uri="jakarta.tags.fmt" %>
<t:layout>
	<h2><c:out value="${translation.appDescriptionHeader}"/></h2>
	
	<c:out value="${translation.appDescriptionHtml}" escapeXml="false"/>
</t:layout>