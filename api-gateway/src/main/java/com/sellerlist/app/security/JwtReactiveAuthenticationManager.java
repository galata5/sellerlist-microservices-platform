package com.sellerlist.app.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.sellerlist.app.auth.service.JwtService;

import reactor.core.publisher.Mono;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

	private final JwtService jwtService;

	public JwtReactiveAuthenticationManager(final JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	public Mono<Authentication> authenticate(final Authentication authentication) {
		final String token = String.valueOf(authentication.getCredentials());
		if (!this.jwtService.isValid(token)) {
			return Mono.error(new BadCredentialsException("Invalid session token."));
		}

		return Mono.just(new UsernamePasswordAuthenticationToken(
			this.jwtService.extractUsername(token),
			token,
			this.jwtService.extractAuthorities(token)));
	}
}
