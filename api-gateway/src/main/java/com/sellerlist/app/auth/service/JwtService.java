package com.sellerlist.app.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.sellerlist.app.auth.model.RoleBasedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final Duration TOKEN_TTL = Duration.ofHours(10);
	private final SecretKey signingKey;

	public JwtService(@Value("${security.jwt.secret}") final String secret) {
		this.signingKey = signingKey(secret);
	}

	public String createToken(final Integer userId, final String username, final RoleBasedAuthority authority) {
		final Instant now = Instant.now();
		return Jwts.builder()
			.subject(username)
			.claim("userId", userId)
			.claim("role", authority.name())
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plus(TOKEN_TTL)))
			.signWith(this.signingKey)
			.compact();
	}

	public Claims parseClaims(final String token) {
		return Jwts.parser()
			.verifyWith(this.signingKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public boolean isValid(final String token) {
		try {
			parseClaims(token);
			return true;
		} catch (final JwtException | IllegalArgumentException exception) {
			return false;
		}
	}

	public String extractUsername(final String token) {
		return parseClaims(token).getSubject();
	}

	public Integer extractUserId(final String token) {
		return parseClaims(token).get("userId", Integer.class);
	}

	public List<GrantedAuthority> extractAuthorities(final String token) {
		final String role = parseClaims(token).get("role", String.class);
		return List.of(new SimpleGrantedAuthority(role));
	}

	public String extractRole(final String token) {
		return parseClaims(token).get("role", String.class);
	}

	private static SecretKey signingKey(final String secret) {
		try {
			final byte[] hash = MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
			return Keys.hmacShaKeyFor(hash);
		} catch (final NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 support is required for JWT signing.", exception);
		}
	}
}
