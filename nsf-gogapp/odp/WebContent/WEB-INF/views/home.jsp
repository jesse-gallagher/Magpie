<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<c:if test="${empty userTokens.active}">
		<fieldset>
			<legend>Log In</legend>
			
			<p>
				Visit <a href="https://login.gog.com/auth?client_id=46899977096215655&layout=client2&redirect_uri=https%3A%2F%2Fembed.gog.com%2Fon_login_success%3Forigin%3Dclient&response_type=code">the GOG login page</a>,
				go through the login progress, and then copy the "code" value from the final URL. Paste and submit it here to add your account.
			</p>
			
			<form method="POST" action="${mvc.basePath}/getUserToken">
				<p><label>Code: <input type="text" name="code" /></label></p>
				<p><input type="submit" value="Submit" /></p>
			</form>
		</fieldset>
	</c:if>
	
	<c:if test="${not empty userTokens.active}">
		<fieldset>
			<legend>Find Games</legend>
			
			<form method="POST" action="${mvc.basePath}/search">
				<p><label>Name: <input type="text" name="search" /></label></p>
				<p><input type="submit" value="Search" /></p>
			</form>
		</fieldset>
	</c:if>
</t:layout>