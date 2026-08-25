package com.sellerlist.app.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sellerlist.app.auth.client.UserCredentialClient;
import com.sellerlist.app.auth.model.AuthCredentialDto;
import com.sellerlist.app.auth.model.request.AuthenticationRequest;
import com.sellerlist.app.auth.model.response.AuthenticatedSession;
import com.sellerlist.app.auth.model.response.AuthenticationResponse;

import reactor.core.publisher.Mono;

@Service
public class AuthenticationService {

	private final UserCredentialClient userCredentialClient;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthenticationService(
			final UserCredentialClient userCredentialClient,
			final PasswordEncoder passwordEncoder,
			final JwtService jwtService) {
		this.userCredentialClient = userCredentialClient;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public Mono<AuthenticatedSession> authenticate(final AuthenticationRequest request) {
		return this.userCredentialClient.findByUsername(request.username())
			.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication credentials.")))
			.flatMap(credential -> {
				if (!this.passwordEncoder.matches(request.password(), credential.password())) {
					return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication credentials."));
				}

				final String token = this.jwtService.createToken(credential.userId(), credential.username(), credential.roleBasedAuthority());
				return Mono.just(new AuthenticatedSession(
						token,
						new AuthenticationResponse(credential.userId(), credential.username(), true)));
			});
	}

	public Mono<AuthenticationResponse> currentSession(final String token) {
		if (!this.jwtService.isValid(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is no longer valid.");
		}

		return Mono.just(new AuthenticationResponse(
				this.jwtService.extractUserId(token),
				this.jwtService.extractUsername(token),
				true));
	}

	public Mono<Boolean> validate(final String token) {
		return Mono.just(this.jwtService.isValid(token));
	}
}
