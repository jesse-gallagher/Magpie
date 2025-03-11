package api.gog.model;

import java.util.List;

public record UserData(
	String country,
	List<Currency> currencies,
	Currency selectedCurrency,
	Language preferredLanguage,
	String ratingBrand,
	boolean isLoggedIn,
	Checksum checksum,
	Updates updates,
	String userId,
	String username,
	String email
) {}
