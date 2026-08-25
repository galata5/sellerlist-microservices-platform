package com.sellerlist.platform.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InternalTokenServiceTest {

	@Test
	void createsTokensThatValidateWithTheSameSecret() {
		final InternalTokenService tokenService = new InternalTokenService(
				"a-long-service-secret-for-tests",
				"API-GATEWAY",
				"USER-SERVICE");

		final String token = tokenService.createToken("api-gateway");

		assertTrue(tokenService.isValid(token));
	}

	@Test
	void rejectsTamperedTokens() {
		final InternalTokenService tokenService = new InternalTokenService(
				"a-long-service-secret-for-tests",
				"API-GATEWAY",
				"USER-SERVICE");

		final String token = tokenService.createToken("api-gateway");

		assertFalse(tokenService.isValid(token + "tampered"));
	}

	@Test
	void extractsBoundAuthenticatedIdentityClaims() {
		final InternalTokenService tokenService = new InternalTokenService(
				"a-long-service-secret-for-tests",
				"API-GATEWAY",
				"ORDER-SERVICE");

		final String token = tokenService.createToken("api-gateway", "12", "seller", "ROLE_USER");

		assertEquals("12", tokenService.extractAuthenticatedUserId(token));
		assertEquals("seller", tokenService.extractAuthenticatedUsername(token));
		assertEquals("ROLE_USER", tokenService.extractAuthenticatedRole(token));
	}
}
