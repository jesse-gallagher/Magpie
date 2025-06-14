<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<t:layout>
	<h2><c:out value="${translation.searchResults}"/></h2>
	
	
	<section class="gallery">
	<c:forEach items="${result.products}" var="product">
		<article>
			<figure>
				<a href="${mvc.basePath}/source/gog/game/${product.id}?tokenId=${tokenId}"><img src="${product.image}.webp" style="" /></a>
				<figcaption><a href="${mvc.basePath}/source/gog/game/${product.id}?tokenId=${tokenId}"><c:out value="${product.title}"/></a></figcaption>
			</figure>
		</article>
	</c:forEach>
	</section>
</t:layout>