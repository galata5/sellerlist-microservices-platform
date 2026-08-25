package com.sellerlist.app.error;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JsonAccessDeniedHandler implements ServerAccessDeniedHandler {

	private final ApiErrorResponseWriter apiErrorResponseWriter;

	public JsonAccessDeniedHandler(final ApiErrorResponseWriter apiErrorResponseWriter) {
		this.apiErrorResponseWriter = apiErrorResponseWriter;
	}

	@Override
	public Mono<Void> handle(final ServerWebExchange exchange, final AccessDeniedException denied) {
		return this.apiErrorResponseWriter.write(
				exchange,
				HttpStatus.FORBIDDEN,
				"You do not have permission to access this endpoint.");
	}
}
