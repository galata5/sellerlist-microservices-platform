package com.sellerlist.app.error;

import java.time.Instant;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class ApiErrorResponseWriter {

	private final ObjectMapper objectMapper;

	public ApiErrorResponseWriter(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public Mono<Void> write(
			final ServerWebExchange exchange,
			final HttpStatus status,
			final String message) {
		final ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		final ApiErrorResponse payload = new ApiErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				exchange.getRequest().getPath().value());

		try {
			final DataBuffer buffer = response.bufferFactory().wrap(this.objectMapper.writeValueAsBytes(payload));
			return response.writeWith(Mono.just(buffer));
		} catch (final JsonProcessingException exception) {
			final DataBuffer buffer = response.bufferFactory()
					.wrap(("{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Failed to serialize error response.\"}")
							.getBytes());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			return response.writeWith(Mono.just(buffer));
		}
	}
}
