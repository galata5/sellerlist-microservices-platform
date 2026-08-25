package com.sellerlist.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import com.sellerlist.app.error.JsonAccessDeniedHandler;
import com.sellerlist.app.error.JsonAuthenticationEntryPoint;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(
			final ServerHttpSecurity http,
			final JwtReactiveAuthenticationManager authenticationManager,
			final JwtServerAuthenticationConverter authenticationConverter,
			final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint,
			final JsonAccessDeniedHandler jsonAccessDeniedHandler) {
		final AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authenticationManager);
		jwtFilter.setServerAuthenticationConverter(authenticationConverter);
		jwtFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));
		jwtFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

		return http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
			.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
			.logout(ServerHttpSecurity.LogoutSpec::disable)
			.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
			.exceptionHandling(spec -> spec
				.authenticationEntryPoint(jsonAuthenticationEntryPoint)
				.accessDeniedHandler(jsonAccessDeniedHandler))
			.addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
			.authorizeExchange(exchanges -> exchanges
				.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.pathMatchers("/api/authenticate/**", "/actuator/health", "/actuator/info").permitAll()
				.pathMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()
				.pathMatchers(HttpMethod.POST, "/api/users/register").permitAll()
				.pathMatchers("/api/credentials/**").denyAll()
				.pathMatchers("/api/favourites/**", "/api/order-items/**").denyAll()
				.pathMatchers(HttpMethod.POST, "/api/users").denyAll()
				.pathMatchers(HttpMethod.PUT, "/api/orders/**", "/api/payments/**").denyAll()
				.pathMatchers(HttpMethod.DELETE, "/api/orders/**", "/api/payments/**").denyAll()
				.pathMatchers(HttpMethod.POST, "/api/payments/**").denyAll()
				.anyExchange().authenticated())
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
