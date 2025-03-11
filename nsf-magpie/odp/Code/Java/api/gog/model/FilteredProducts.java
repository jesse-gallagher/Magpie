package api.gog.model;

import java.util.List;

public record FilteredProducts(
	List<Product> products
) {}
