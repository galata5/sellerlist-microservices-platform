package com.sellerlist.app.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AppConstant {
	
	public static final String LOCAL_DATE_FORMAT = "dd-MM-yyyy";
	public static final String LOCAL_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
	public static final String ZONED_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
	public static final String INSTANT_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
	
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public abstract static class DiscoveredDomainsApi {
		
		public static final String USER_SERVICE_HOST = serviceUrl("USER_SERVICE_BASE_URL", "http://user-service:8700", "/user-service");
		public static final String USER_SERVICE_API_URL = USER_SERVICE_HOST + "/api/users";
		
		public static final String PRODUCT_SERVICE_HOST = serviceUrl("PRODUCT_SERVICE_BASE_URL", "http://product-service:8500", "/product-service");
		public static final String PRODUCT_SERVICE_API_URL = PRODUCT_SERVICE_HOST + "/api/products";
		
		public static final String ORDER_SERVICE_HOST = serviceUrl("ORDER_SERVICE_BASE_URL", "http://order-service:8300", "/order-service");
		public static final String ORDER_SERVICE_API_URL = ORDER_SERVICE_HOST + "/api/orders";
		
		public static final String FAVOURITE_SERVICE_HOST = serviceUrl("FAVOURITE_SERVICE_BASE_URL", "http://favourite-service:8800", "/favourite-service");
		public static final String FAVOURITE_SERVICE_API_URL = FAVOURITE_SERVICE_HOST + "/api/favourites";
		
		public static final String PAYMENT_SERVICE_HOST = serviceUrl("PAYMENT_SERVICE_BASE_URL", "http://payment-service:8400", "/payment-service");
		public static final String PAYMENT_SERVICE_API_URL = PAYMENT_SERVICE_HOST + "/api/payments";
		
		public static final String SHIPPING_SERVICE_HOST = serviceUrl("SHIPPING_SERVICE_BASE_URL", "http://shipping-service:8600", "/shipping-service");
		public static final String SHIPPING_SERVICE_API_URL = SHIPPING_SERVICE_HOST + "/api/order-items";
		
		private static String serviceUrl(final String envVar, final String defaultBaseUrl, final String contextPath) {
			final String baseUrl = System.getenv().getOrDefault(envVar, defaultBaseUrl);
			return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) + contextPath : baseUrl + contextPath;
		}
		
	}
	
	
	
}







