package com.sellerlist.app.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.WebExceptionHandler;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayJsonErrorHandler implements WebExceptionHandler {

	private final ApiErrorResponseWriter apiErrorResponseWriter;

	public GatewayJsonErrorHandler(final ApiErrorResponseWriter apiErrorResponseWriter) {
		this.apiErrorResponseWriter = apiErrorResponseWriter;
	}

	@Override
	public Mono<Void> handle(final ServerWebExchange exchange, final Throwable exception) {
		if (exchange.getResponse().isCommitted()) {
			return Mono.error(exception);
		}

		final HttpStatus status = resolveStatus(exception);
		final String message = resolveMessage(exception, status);
		return this.apiErrorResponseWriter.write(exchange, status, message);
	}

	private HttpStatus resolveStatus(final Throwable exception) {
		if (exception instanceof ResponseStatusException responseStatusException) {
			return toHttpStatus(responseStatusException.getStatusCode());
		}
		if (exception instanceof ServerWebInputException) {
			return HttpStatus.BAD_REQUEST;
		}
		if (exception instanceof AuthenticationException) {
			return HttpStatus.UNAUTHORIZED;
		}
		if (exception instanceof AccessDeniedException) {
			return HttpStatus.FORBIDDEN;
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	private String resolveMessage(final Throwable exception, final HttpStatus status) {
		if (exception instanceof ResponseStatusException responseStatusException) {
			return responseStatusException.getReason() != null
					? responseStatusException.getReason()
					: status.getReasonPhrase();
		}
		if (exception instanceof ServerWebInputException && exception.getMessage() != null) {
			return "The request payload or parameters are not valid.";
		}
		if (exception instanceof AuthenticationException) {
			return "Authentication is required to access this endpoint.";
		}
		if (exception instanceof AccessDeniedException) {
			return "You do not have permission to access this endpoint.";
		}
		return "The gateway could not process the request.";
	}

	private HttpStatus toHttpStatus(final HttpStatusCode statusCode) {
		return HttpStatus.resolve(statusCode.value()) != null
				? HttpStatus.resolve(statusCode.value())
				: HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
