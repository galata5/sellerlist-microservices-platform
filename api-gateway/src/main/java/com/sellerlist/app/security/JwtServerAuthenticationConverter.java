package com.sellerlist.app.security;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

	@Override
	public Mono<Authentication> convert(final ServerWebExchange exchange) {
		if (isPublicEndpoint(exchange)) {
			return Mono.empty();
		}

		final String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
		final HttpCookie cookie = exchange.getRequest().getCookies().getFirst(TokenExtractor.SESSION_COOKIE);
		final String token = TokenExtractor.extract(authorizationHeader, cookie != null ? cookie.getValue() : null);
		if (token == null) {
			return Mono.empty();
		}

		return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
	}

	private boolean isPublicEndpoint(final ServerWebExchange exchange) {
		final HttpMethod method = exchange.getRequest().getMethod();
		final String path = exchange.getRequest().getPath().value();

		if (method == HttpMethod.OPTIONS) {
			return true;
		}
		if (path.startsWith("/api/authenticate/") || "/api/authenticate".equals(path)) {
			return true;
		}
		if ("/actuator/health".equals(path) || "/actuator/info".equals(path)) {
			return true;
		}
		if (method == HttpMethod.POST && "/api/users/register".equals(path)) {
			return true;
		}
		if (method == HttpMethod.GET && (path.startsWith("/api/products/") || "/api/products".equals(path))) {
			return true;
		}
		return method == HttpMethod.GET && (path.startsWith("/api/categories/") || "/api/categories".equals(path));
	}
}
