package com.sellerlist.platform.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public final class InternalServiceAuthenticationFilter extends OncePerRequestFilter {

	private final InternalTokenService tokenService;
	private final Set<String> publicApiPathSuffixes;

	public InternalServiceAuthenticationFilter(final InternalTokenService tokenService) {
		this(tokenService, Set.of());
	}

	public InternalServiceAuthenticationFilter(
			final InternalTokenService tokenService,
			final Set<String> publicApiPathSuffixes) {
		this.tokenService = tokenService;
		this.publicApiPathSuffixes = Set.copyOf(publicApiPathSuffixes);
	}

	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) {
		final String path = request.getRequestURI();
		return request.getMethod().equalsIgnoreCase("OPTIONS")
			|| !path.contains("/api/")
			|| path.contains("/actuator/")
			|| this.publicApiPathSuffixes.stream().anyMatch(path::endsWith);
	}

	@Override
	protected void doFilterInternal(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {
		final String header = request.getHeader(InternalRequestHeaders.INTERNAL_AUTHORIZATION);
		final String token = extractToken(header);

		if (token == null || !this.tokenService.isValid(token)) {
			response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid internal service token.");
			return;
		}

		filterChain.doFilter(new AuthenticatedRequestWrapper(
				request,
				this.tokenService.extractAuthenticatedUserId(token),
				this.tokenService.extractAuthenticatedUsername(token),
				this.tokenService.extractAuthenticatedRole(token)), response);
	}

	private static String extractToken(final String header) {
		if (header == null || header.isBlank()) {
			return null;
		}
		return header.startsWith("Bearer ") ? header.substring(7).trim() : header.trim();
	}

	private static final class AuthenticatedRequestWrapper extends HttpServletRequestWrapper {

		private final Map<String, List<String>> injectedHeaders;
		private final String authenticatedUserId;
		private final String authenticatedUsername;
		private final String authenticatedRole;

		private AuthenticatedRequestWrapper(
				final HttpServletRequest request,
				final String authenticatedUserId,
				final String authenticatedUsername,
				final String authenticatedRole) {
			super(request);
			this.authenticatedUserId = authenticatedUserId;
			this.authenticatedUsername = authenticatedUsername;
			this.authenticatedRole = authenticatedRole;
			this.injectedHeaders = new LinkedHashMap<>();
			putHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID, authenticatedUserId);
			putHeader(InternalRequestHeaders.AUTHENTICATED_USERNAME, authenticatedUsername);
			putHeader(InternalRequestHeaders.AUTHENTICATED_ROLE, authenticatedRole);
		}

		@Override
		public String getHeader(final String name) {
			final List<String> values = this.injectedHeaders.get(normalize(name));
			if (values != null && !values.isEmpty()) {
				return values.get(0);
			}
			return super.getHeader(name);
		}

		@Override
		public Enumeration<String> getHeaders(final String name) {
			final List<String> values = this.injectedHeaders.get(normalize(name));
			if (values != null) {
				return Collections.enumeration(values);
			}
			return super.getHeaders(name);
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			final List<String> names = new ArrayList<>(Collections.list(super.getHeaderNames()));
			for (final String injectedName : this.injectedHeaders.keySet()) {
				if (names.stream().noneMatch(existing -> injectedName.equalsIgnoreCase(existing))) {
					names.add(injectedName);
				}
			}
			return new Vector<>(names).elements();
		}

		private void putHeader(final String name, final String value) {
			if (value != null && !value.isBlank()) {
				this.injectedHeaders.put(normalize(name), List.of(value));
			}
		}

		private static String normalize(final String name) {
			return name == null ? "" : name.toLowerCase();
		}
	}
}
