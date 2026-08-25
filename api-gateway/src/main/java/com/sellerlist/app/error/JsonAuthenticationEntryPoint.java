package com.sellerlist.app.error;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JsonAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	private final ApiErrorResponseWriter apiErrorResponseWriter;

	public JsonAuthenticationEntryPoint(final ApiErrorResponseWriter apiErrorResponseWriter) {
		this.apiErrorResponseWriter = apiErrorResponseWriter;
	}

	@Override
	public Mono<Void> commence(final ServerWebExchange exchange, final AuthenticationException exception) {
		return this.apiErrorResponseWriter.write(
				exchange,
				HttpStatus.UNAUTHORIZED,
				"Authentication is required to access this endpoint.");
	}
}
