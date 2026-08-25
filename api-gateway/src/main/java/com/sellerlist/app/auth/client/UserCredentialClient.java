package com.sellerlist.app.auth.client;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.sellerlist.app.auth.model.AuthCredentialDto;
import com.sellerlist.platform.security.InternalRequestHeaders;
import com.sellerlist.platform.security.InternalTokenService;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class UserCredentialClient {

	private final WebClient webClient;
	private final InternalTokenService internalTokenService;
	private final String serviceName;

	public UserCredentialClient(
			final WebClient.Builder webClientBuilder,
			@Value("${auth.user-service.base-url}") final String userServiceBaseUrl,
			@Value("${security.internal.user-service-secret}") final String internalServiceSecret,
			@Value("${security.internal.issuer}") final String issuer,
			@Value("${spring.application.name}") final String serviceName) {
		this.webClient = webClientBuilder.baseUrl(userServiceBaseUrl).build();
		this.internalTokenService = new InternalTokenService(internalServiceSecret, issuer, "USER-SERVICE");
		this.serviceName = serviceName;
	}

	public Mono<AuthCredentialDto> findByUsername(final String username) {
		return this.webClient.get()
			.uri("/api/credentials/internal/username/{username}", username)
			.header(
					InternalRequestHeaders.INTERNAL_AUTHORIZATION,
					"Bearer " + this.internalTokenService.createToken(this.serviceName))
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, response -> response.createException()
				.flatMap(exception -> Mono.error(
					new ResponseStatusException(response.statusCode(), "Invalid authentication credentials.", exception))))
			.bodyToMono(AuthCredentialDto.class)
			.timeout(Duration.ofSeconds(5))
			.retryWhen(Retry.backoff(3, Duration.ofMillis(400))
				.filter(exception -> exception instanceof WebClientRequestException || exception instanceof TimeoutException))
			.onErrorMap(WebClientRequestException.class, exception -> new ResponseStatusException(
					HttpStatus.SERVICE_UNAVAILABLE,
					"Authentication service is still starting. Please retry in a few seconds.",
					exception))
			.onErrorMap(TimeoutException.class, exception -> new ResponseStatusException(
					HttpStatus.SERVICE_UNAVAILABLE,
					"Authentication service timed out. Please retry in a few seconds.",
					exception));
	}
}
