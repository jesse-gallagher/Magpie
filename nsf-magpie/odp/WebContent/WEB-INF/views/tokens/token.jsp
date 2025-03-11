<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h2><c:out value="${translation.userToken}"/></h2>
	
	<dl>
		<dt><c:out value="${translation.serviceType}"/></dt>
		<dd><c:out value="${token.serviceType}"/></dd>
		
		<dt><c:out value="${translation.username}"/></dt>
		<dd><c:out value="${token.username}"/></dd>
	</dl>
	
	
</t:layout>