package com.sellerlist.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.sellerlist.app.auth.service.JwtService;
import com.sellerlist.platform.security.InternalRequestHeaders;
import com.sellerlist.platform.security.InternalTokenService;
import com.sellerlist.app.security.TokenExtractor;

import reactor.core.publisher.Mono;

@Component
public class InternalServiceRelayFilter implements GlobalFilter, Ordered {

	private final JwtService jwtService;
	private final String serviceName;
	private final InternalTokenService userServiceTokenService;
	private final InternalTokenService productServiceTokenService;
	private final InternalTokenService orderServiceTokenService;
	private final InternalTokenService paymentServiceTokenService;
	private final InternalTokenService shippingServiceTokenService;
	private final InternalTokenService favouriteServiceTokenService;

	public InternalServiceRelayFilter(
			final JwtService jwtService,
			@Value("${security.internal.issuer}") final String issuer,
			@Value("${spring.application.name}") final String serviceName,
			@Value("${security.internal.user-service-secret}") final String userServiceSecret,
			@Value("${security.internal.product-service-secret}") final String productServiceSecret,
			@Value("${security.internal.order-service-secret}") final String orderServiceSecret,
			@Value("${security.internal.payment-service-secret}") final String paymentServiceSecret,
			@Value("${security.internal.shipping-service-secret}") final String shippingServiceSecret,
			@Value("${security.internal.favourite-service-secret}") final String favouriteServiceSecret) {
		this.jwtService = jwtService;
		this.serviceName = serviceName;
		this.userServiceTokenService = new InternalTokenService(userServiceSecret, issuer, "USER-SERVICE");
		this.productServiceTokenService = new InternalTokenService(productServiceSecret, issuer, "PRODUCT-SERVICE");
		this.orderServiceTokenService = new InternalTokenService(orderServiceSecret, issuer, "ORDER-SERVICE");
		this.paymentServiceTokenService = new InternalTokenService(paymentServiceSecret, issuer, "PAYMENT-SERVICE");
		this.shippingServiceTokenService = new InternalTokenService(shippingServiceSecret, issuer, "SHIPPING-SERVICE");
		this.favouriteServiceTokenService = new InternalTokenService(favouriteServiceSecret, issuer, "FAVOURITE-SERVICE");
	}

	@Override
	public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
		if (!exchange.getRequest().getPath().value().startsWith("/api/")) {
			return chain.filter(exchange);
		}

		final String path = exchange.getRequest().getPath().value();
		final InternalTokenService targetTokenService = resolveTokenService(path);
		if (targetTokenService == null) {
			return chain.filter(exchange);
		}

		final String externalToken = extractExternalToken(exchange);
		final ServerHttpRequest request = exchange.getRequest().mutate().headers(headers -> {
			headers.remove(InternalRequestHeaders.INTERNAL_AUTHORIZATION);
			final String authenticatedUserId = externalToken != null && this.jwtService.isValid(externalToken)
					? String.valueOf(this.jwtService.extractUserId(externalToken))
					: null;
			final String authenticatedUsername = externalToken != null && this.jwtService.isValid(externalToken)
					? this.jwtService.extractUsername(externalToken)
					: null;
			final String authenticatedRole = externalToken != null && this.jwtService.isValid(externalToken)
					? this.jwtService.extractRole(externalToken)
					: null;
			if (externalToken != null && this.jwtService.isValid(externalToken)) {
				headers.remove(InternalRequestHeaders.AUTHENTICATED_USERNAME);
				headers.remove(InternalRequestHeaders.AUTHENTICATED_ROLE);
				headers.remove(InternalRequestHeaders.AUTHENTICATED_USER_ID);
			}
			headers.set(
					InternalRequestHeaders.INTERNAL_AUTHORIZATION,
					"Bearer " + targetTokenService.createToken(
							this.serviceName,
							authenticatedUserId,
							authenticatedUsername,
							authenticatedRole));
		}).build();

		return chain.filter(exchange.mutate().request(request).build());
	}

	private InternalTokenService resolveTokenService(final String path) {
		if (matchesApiPath(path, "/api/users", "/api/credentials", "/api/address", "/api/verificationTokens")) {
			return this.userServiceTokenService;
		}
		if (matchesApiPath(path, "/api/products", "/api/categories")) {
			return this.productServiceTokenService;
		}
		if (matchesApiPath(path, "/api/orders", "/api/carts")) {
			return this.orderServiceTokenService;
		}
		if (matchesApiPath(path, "/api/payments")) {
			return this.paymentServiceTokenService;
		}
		if (matchesApiPath(path, "/api/order-items")) {
			return this.shippingServiceTokenService;
		}
		if (matchesApiPath(path, "/api/favourites")) {
			return this.favouriteServiceTokenService;
		}
		return null;
	}

	private static boolean matchesApiPath(final String path, final String... prefixes) {
		for (final String prefix : prefixes) {
			if (path.equals(prefix) || path.startsWith(prefix + "/")) {
				return true;
			}
		}
		return false;
	}

	private String extractExternalToken(final ServerWebExchange exchange) {
		final String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
		final HttpCookie cookie = exchange.getRequest().getCookies().getFirst(TokenExtractor.SESSION_COOKIE);
		return TokenExtractor.extract(authorizationHeader, cookie != null ? cookie.getValue() : null);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
