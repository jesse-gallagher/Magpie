<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="fn" uri="jakarta.tags.fmt" %>
<t:layout>
	<h2><c:out value="${translation.userToken}"/></h2>
	
	<dl>
		<dt><c:out value="${translation.serviceType}"/></dt>
		<dd><c:out value="${token.serviceType}"/></dd>
		
		<dt><c:out value="${translation.username}"/></dt>
		<dd><c:out value="${token.username}"/></dd>
	</dl>
	
	
</t:layout>