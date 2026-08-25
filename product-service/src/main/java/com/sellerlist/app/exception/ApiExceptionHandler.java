package com.sellerlist.app.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.sellerlist.app.exception.payload.ExceptionMsg;
import com.sellerlist.app.exception.wrapper.CategoryNotFoundException;
import com.sellerlist.app.exception.wrapper.ProductNotFoundException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionMsg> handleValidationException(final MethodArgumentNotValidException error) {
		final String message = error.getBindingResult().getFieldError() != null
				? error.getBindingResult().getFieldError().getDefaultMessage()
				: "Request body is invalid";
		return build(HttpStatus.BAD_REQUEST, message, error);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionMsg> handleUnreadableMessage(final HttpMessageNotReadableException error) {
		return build(HttpStatus.BAD_REQUEST, "Request body is invalid", error);
	}

	@ExceptionHandler({ CategoryNotFoundException.class, ProductNotFoundException.class })
	public ResponseEntity<ExceptionMsg> handleNotFound(final RuntimeException error) {
		return build(HttpStatus.NOT_FOUND, error.getMessage(), error);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ExceptionMsg> handleResponseStatus(final ResponseStatusException error) {
		final HttpStatus status = HttpStatus.valueOf(error.getStatusCode().value());
		return build(status, error.getReason(), error);
	}

	@ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
	public ResponseEntity<ExceptionMsg> handleBadRequest(final RuntimeException error) {
		return build(HttpStatus.BAD_REQUEST, error.getMessage(), error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionMsg> handleUnexpected(final Exception error) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error.", error);
	}

	private ResponseEntity<ExceptionMsg> build(
			final HttpStatus status,
			final String message,
			final Exception error) {
		log.error("API request failed with status {}", status.value(), error);
		return ResponseEntity.status(status).body(ExceptionMsg.builder()
				.timestamp(OffsetDateTime.now().toString())
				.httpStatus(status)
				.msg(message == null || message.isBlank() ? status.getReasonPhrase() : message)
				.build());
	}
}







