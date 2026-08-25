package com.sellerlist.app.config;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GatewayStartupValidation {

	private final String corsAllowedOrigins;
	private final String jwtSecret;
	private final String userServiceInternalAuthSecret;
	private final String productServiceInternalAuthSecret;
	private final String orderServiceInternalAuthSecret;
	private final String paymentServiceInternalAuthSecret;
	private final String shippingServiceInternalAuthSecret;
	private final String favouriteServiceInternalAuthSecret;
	private final boolean secureCookies;

	public GatewayStartupValidation(
			@Value("${CORS_ALLOWED_ORIGINS}") final String corsAllowedOrigins,
			@Value("${security.jwt.secret}") final String jwtSecret,
			@Value("${security.internal.user-service-secret}") final String userServiceInternalAuthSecret,
			@Value("${security.internal.product-service-secret}") final String productServiceInternalAuthSecret,
			@Value("${security.internal.order-service-secret}") final String orderServiceInternalAuthSecret,
			@Value("${security.internal.payment-service-secret}") final String paymentServiceInternalAuthSecret,
			@Value("${security.internal.shipping-service-secret}") final String shippingServiceInternalAuthSecret,
			@Value("${security.internal.favourite-service-secret}") final String favouriteServiceInternalAuthSecret,
			@Value("${security.cookies.secure}") final boolean secureCookies) {
		this.corsAllowedOrigins = corsAllowedOrigins;
		this.jwtSecret = jwtSecret;
		this.userServiceInternalAuthSecret = userServiceInternalAuthSecret;
		this.productServiceInternalAuthSecret = productServiceInternalAuthSecret;
		this.orderServiceInternalAuthSecret = orderServiceInternalAuthSecret;
		this.paymentServiceInternalAuthSecret = paymentServiceInternalAuthSecret;
		this.shippingServiceInternalAuthSecret = shippingServiceInternalAuthSecret;
		this.favouriteServiceInternalAuthSecret = favouriteServiceInternalAuthSecret;
		this.secureCookies = secureCookies;
	}

	@PostConstruct
	void validate() {
		requireNonBlank(this.corsAllowedOrigins, "CORS_ALLOWED_ORIGINS");
		requireNonBlank(this.jwtSecret, "JWT_SECRET");
		requireServiceSecret(this.userServiceInternalAuthSecret, "USER_SERVICE_INTERNAL_AUTH_SECRET");
		requireServiceSecret(this.productServiceInternalAuthSecret, "PRODUCT_SERVICE_INTERNAL_AUTH_SECRET");
		requireServiceSecret(this.orderServiceInternalAuthSecret, "ORDER_SERVICE_INTERNAL_AUTH_SECRET");
		requireServiceSecret(this.paymentServiceInternalAuthSecret, "PAYMENT_SERVICE_INTERNAL_AUTH_SECRET");
		requireServiceSecret(this.shippingServiceInternalAuthSecret, "SHIPPING_SERVICE_INTERNAL_AUTH_SECRET");
		requireServiceSecret(this.favouriteServiceInternalAuthSecret, "FAVOURITE_SERVICE_INTERNAL_AUTH_SECRET");
		if (this.jwtSecret.length() < 32) {
			throw new IllegalStateException("JWT_SECRET must be at least 32 characters long.");
		}
		if (!this.secureCookies && !allowsInsecureLocalCookies()) {
			throw new IllegalStateException(
					"SECURE_COOKIES may be false only for explicit localhost-based development origins.");
		}
	}

	private boolean allowsInsecureLocalCookies() {
		final String normalizedOrigins = this.corsAllowedOrigins.toLowerCase();
		return normalizedOrigins.contains("localhost") || normalizedOrigins.contains("127.0.0.1");
	}

	private static void requireServiceSecret(final String value, final String propertyName) {
		requireNonBlank(value, propertyName);
		if (value.length() < 32) {
			throw new IllegalStateException(propertyName + " must be at least 32 characters long.");
		}
	}

	private static void requireNonBlank(final String value, final String propertyName) {
		if (value == null || value.isBlank()) {
			throw new IllegalStateException(propertyName + " must be set.");
		}
	}
}
