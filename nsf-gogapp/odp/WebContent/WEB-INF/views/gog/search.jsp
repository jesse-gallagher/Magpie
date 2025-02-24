<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<h2><c:out value="${translation.searchResults}"/></h2>
	
	
	<section class="gallery">
	<c:forEach items="${result.products}" var="product">
		<article>
			<figure>
				<a href="${mvc.basePath}/gog/game/${product.id}?tokenId=${tokenId}"><img src="${product.image}.webp" style="" /></a>
				<figcaption><a href="${mvc.basePath}/gog/game/${product.id}?tokenId=${tokenId}"><c:out value="${product.title}"/></a></figcaption>
			</figure>
		</article>
	</c:forEach>
	</section>
</t:layout>