package com.sellerlist.platform.security;

public final class InternalRequestHeaders {

	public static final String INTERNAL_AUTHORIZATION = "X-Internal-Authorization";
	public static final String AUTHENTICATED_USER_ID = "X-Authenticated-User-Id";
	public static final String AUTHENTICATED_USERNAME = "X-Authenticated-Username";
	public static final String AUTHENTICATED_ROLE = "X-Authenticated-Role";

	private InternalRequestHeaders() {
	}
}
