package com.sellerlist.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.sellerlist.platform.security.InternalServiceAuthenticationFilter;
import com.sellerlist.platform.security.InternalTokenService;

@Configuration
public class InternalServiceSecurityConfig {

	@Bean
	public InternalTokenService internalTokenService(
			@Value("${security.internal.secret}") final String secret,
			@Value("${security.internal.expected-issuer}") final String issuer,
			@Value("${spring.application.name}") final String audience) {
		return new InternalTokenService(secret, issuer, audience);
	}

	@Bean
	public FilterRegistrationBean<InternalServiceAuthenticationFilter> internalServiceAuthenticationFilter(
			final InternalTokenService internalTokenService) {
		final FilterRegistrationBean<InternalServiceAuthenticationFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new InternalServiceAuthenticationFilter(internalTokenService));
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registration;
	}
}
