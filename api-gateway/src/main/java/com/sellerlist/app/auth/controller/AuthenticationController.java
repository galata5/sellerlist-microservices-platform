package com.sellerlist.app.auth.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sellerlist.app.auth.model.request.AuthenticationRequest;
import com.sellerlist.app.auth.model.response.AuthenticationResponse;
import com.sellerlist.app.auth.service.AuthenticationService;
import com.sellerlist.app.security.TokenExtractor;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/authenticate")
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	private final boolean secureCookies;

	public AuthenticationController(
			final AuthenticationService authenticationService,
			@Value("${security.cookies.secure}") final boolean secureCookies) {
		this.authenticationService = authenticationService;
		this.secureCookies = secureCookies;
	}

	@PostMapping
	public Mono<ResponseEntity<AuthenticationResponse>> authenticate(@Valid @RequestBody final AuthenticationRequest request) {
		return this.authenticationService.authenticate(request)
			.map(session -> ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, sessionCookie(session.token(), false).toString())
				.body(session.response()));
	}

	@PostMapping("/jwt/validate")
	public Mono<ResponseEntity<Boolean>> validateJwt(@RequestHeader(value = "Authorization", required = false) final String authorizationHeader) {
		final String token = TokenExtractor.extract(authorizationHeader, null);
		if (token == null) {
			return Mono.just(ResponseEntity.badRequest().body(false));
		}
		return this.authenticationService.validate(token)
			.map(ResponseEntity::ok);
	}

	@GetMapping("/session")
	public Mono<ResponseEntity<AuthenticationResponse>> currentSession(
			@RequestHeader(value = "Authorization", required = false) final String authorizationHeader,
			@org.springframework.web.bind.annotation.CookieValue(name = TokenExtractor.SESSION_COOKIE, required = false) final String sessionCookie) {
		final String token = TokenExtractor.extract(authorizationHeader, sessionCookie);
		if (token == null) {
			return Mono.just(ResponseEntity.ok(new AuthenticationResponse(null, null, false)));
		}
		try {
			return this.authenticationService.currentSession(token)
				.map(ResponseEntity::ok);
		} catch (final ResponseStatusException exception) {
			if (exception.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
				return Mono.just(ResponseEntity.ok(new AuthenticationResponse(null, null, false)));
			}
			throw exception;
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		return ResponseEntity.noContent()
			.header(HttpHeaders.SET_COOKIE, sessionCookie("", true).toString())
			.build();
	}

	private ResponseCookie sessionCookie(final String token, final boolean expired) {
		return ResponseCookie.from(TokenExtractor.SESSION_COOKIE, token)
			.httpOnly(true)
			.secure(this.secureCookies)
			.path("/")
			.sameSite("Strict")
			.maxAge(expired ? 0 : 60 * 60 * 10)
			.build();
	}
}
