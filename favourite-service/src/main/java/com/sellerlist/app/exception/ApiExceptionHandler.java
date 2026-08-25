package com.sellerlist.app.exception;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationException(final MethodArgumentNotValidException error) {
		final String message = error.getBindingResult().getFieldError() != null
				? error.getBindingResult().getFieldError().getDefaultMessage()
				: "Request body is invalid";
		return build(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, Object>> handleUnreadableMessage(final HttpMessageNotReadableException error) {
		return build(HttpStatus.BAD_REQUEST, "Request body is invalid");
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> handleResponseStatus(final ResponseStatusException error) {
		return build(HttpStatus.valueOf(error.getStatusCode().value()), error.getReason());
	}

	@ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
	public ResponseEntity<Map<String, Object>> handleBadRequest(final RuntimeException error) {
		return build(HttpStatus.BAD_REQUEST, error.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleUnexpected(final Exception error) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error.");
	}

	private ResponseEntity<Map<String, Object>> build(final HttpStatus status, final String message) {
		return ResponseEntity.status(status).body(Map.of(
				"timestamp", OffsetDateTime.now().toString(),
				"status", status.value(),
				"error", status.getReasonPhrase(),
				"message", message == null ? status.getReasonPhrase() : message));
	}
}
