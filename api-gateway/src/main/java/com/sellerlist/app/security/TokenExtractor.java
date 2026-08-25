package com.sellerlist.app.security;

public final class TokenExtractor {

	public static final String SESSION_COOKIE = "sellerlist_session";

	private TokenExtractor() {
	}

	public static String extract(final String authorizationHeader, final String cookieToken) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return cookieToken == null || cookieToken.isBlank() ? null : cookieToken;
	}
}
