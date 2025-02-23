<%@tag description="Overall Page template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="${translation._lang}">
	<head>
		<meta http-equiv="x-ua-compatible" content="ie=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no" />
		
		<base href="${pageContext.request.contextPath}/" />
		
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/$Icon" />
		<link rel="apple-touch-icon" sizes="32x32" href="${pageContext.request.contextPath}/$Icon" />
		<link rel="stylesheet" href="css/milligram.css" />
		<link rel="stylesheet" href="css/style.css" />
		
		<title><c:out value="${translation.appTitle}"/></title>
	</head>
	<body>
		<nav id="main-nav">
			<input id="navbar-toggle" class="mobile-nav" type="checkbox" aria-hidden="true" />
			
			<h1><a href="${mvc.basePath}"><c:out value="${translation.appTitle}"/></a></h1>
			
			<ul class="links">
				<li><a href="${mvc.basePath}/"><c:out value="${translation.home}"/></a></li>
				<li><a href="${mvc.basePath}/library"><c:out value="${translation.library}"/></a></li>
			</ul>
			
			<hr />
			
			<p><c:out value="${encoder.abbreviateName(userBean.id)}"/></p>
			<a href="${pageContext.request.contextPath}?Logout&RedirectTo=${encoder.urlEncode(mvc.basePath)}">
				<c:out value="${translation.logoutLink}"/>
			</a>
		</nav>
		<section id="main-body">
			<jsp:doBody />
		</section>
	</body>
</html>