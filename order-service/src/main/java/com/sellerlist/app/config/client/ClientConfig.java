package com.sellerlist.app.config.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.sellerlist.platform.security.InternalServiceClientInterceptor;
import com.sellerlist.platform.security.InternalTokenService;

@Configuration
public class ClientConfig {

	@Bean
	RestTemplate orderServiceRestTemplate(
			final RestTemplateBuilder builder,
			@Value("${security.internal.catalog-service-secret}") final String catalogServiceSecret,
			@Value("${security.internal.expected-issuer}") final String issuer,
			@Value("${spring.application.name}") final String callerService) {
		final InternalTokenService catalogTokenService = new InternalTokenService(
				catalogServiceSecret,
				issuer,
				"PRODUCT-SERVICE");
		return builder
				.setConnectTimeout(Duration.ofSeconds(3))
				.setReadTimeout(Duration.ofSeconds(5))
				.additionalInterceptors(new InternalServiceClientInterceptor(catalogTokenService, callerService))
				.build();
	}
}
