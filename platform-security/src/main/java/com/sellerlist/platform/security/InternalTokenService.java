package com.sellerlist.platform.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public final class InternalTokenService {

	private static final Duration TOKEN_TTL = Duration.ofMinutes(5);
	private static final String USER_ID_CLAIM = "uid";
	private static final String USERNAME_CLAIM = "usr";
	private static final String ROLE_CLAIM = "rol";
	private final SecretKey signingKey;
	private final String issuer;
	private final String audience;

	public InternalTokenService(final String secret, final String issuer, final String audience) {
		this.signingKey = signingKey(secret);
		this.issuer = issuer;
		this.audience = audience;
	}

	public String createToken(final String callerService) {
		return this.createToken(callerService, null, null, null);
	}

	public String createToken(
			final String callerService,
			final String authenticatedUserId,
			final String authenticatedUsername,
			final String authenticatedRole) {
		final Instant now = Instant.now();
		final io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
			.issuer(this.issuer)
			.subject(callerService)
			.audience().add(this.audience).and()
			.claim("internal", true)
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plus(TOKEN_TTL)))
			.signWith(this.signingKey);
		if (authenticatedUserId != null) {
			builder.claim(USER_ID_CLAIM, authenticatedUserId);
		}
		if (authenticatedUsername != null) {
			builder.claim(USERNAME_CLAIM, authenticatedUsername);
		}
		if (authenticatedRole != null) {
			builder.claim(ROLE_CLAIM, authenticatedRole);
		}
		return builder.compact();
	}

	public boolean isValid(final String token) {
		try {
			final Claims claims = Jwts.parser()
				.verifyWith(this.signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
			return Boolean.TRUE.equals(claims.get("internal", Boolean.class))
					&& this.issuer.equals(claims.getIssuer())
					&& claims.getAudience() != null
					&& claims.getAudience().contains(this.audience);
		} catch (final JwtException | IllegalArgumentException exception) {
			return false;
		}
	}

	public String extractSubject(final String token) {
		return claims(token).getSubject();
	}

	public String extractAuthenticatedUserId(final String token) {
		return Objects.toString(claims(token).get(USER_ID_CLAIM), null);
	}

	public String extractAuthenticatedUsername(final String token) {
		return Objects.toString(claims(token).get(USERNAME_CLAIM), null);
	}

	public String extractAuthenticatedRole(final String token) {
		return Objects.toString(claims(token).get(ROLE_CLAIM), null);
	}

	private Claims claims(final String token) {
		return Jwts.parser()
			.verifyWith(this.signingKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private static SecretKey signingKey(final String secret) {
		try {
			final byte[] hash = MessageDigest.getInstance("SHA-256")
				.digest(secret.getBytes(StandardCharsets.UTF_8));
			return Keys.hmacShaKeyFor(hash);
		} catch (final NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 support is required for internal token signing.", exception);
		}
	}
}
