package com.sellerlist.app.config;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class AuthRateLimitFilter implements WebFilter, Ordered {

	private static final long WINDOW_SECONDS = 60;
	private static final int MAX_REQUESTS_PER_WINDOW = 15;
	private final Map<String, RateWindow> requests = new ConcurrentHashMap<>();

	@Override
	public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
		final String path = exchange.getRequest().getPath().value();
		if (!isProtectedEdgePath(path)) {
			return chain.filter(exchange);
		}

		final String key = clientKey(exchange.getRequest(), path);
		final long now = Instant.now().getEpochSecond();
		final RateWindow window = this.requests.compute(key, (ignored, current) -> current == null || current.windowStart + WINDOW_SECONDS <= now
				? new RateWindow(now, 1)
				: current.increment());

		if (window.requestCount > MAX_REQUESTS_PER_WINDOW) {
			exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
			return exchange.getResponse().setComplete();
		}

		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 10;
	}

	private static boolean isProtectedEdgePath(final String path) {
		return path.equals("/api/authenticate")
			|| path.equals("/api/users/register");
	}

	private static String clientKey(final ServerHttpRequest request, final String path) {
		final InetSocketAddress remoteAddress = request.getRemoteAddress();
		final String host = remoteAddress == null ? "unknown" : remoteAddress.getAddress().getHostAddress();
		return host + ":" + path;
	}

	private record RateWindow(long windowStart, int requestCount) {
		private RateWindow increment() {
			return new RateWindow(this.windowStart, this.requestCount + 1);
		}
	}
}
