package com.sellerlist.platform.security;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public final class InternalServiceClientInterceptor implements ClientHttpRequestInterceptor {

	private final InternalTokenService tokenService;
	private final String callerService;

	public InternalServiceClientInterceptor(final InternalTokenService tokenService, final String callerService) {
		this.tokenService = tokenService;
		this.callerService = callerService;
	}

	@Override
	public ClientHttpResponse intercept(
			final HttpRequest request,
			final byte[] body,
			final ClientHttpRequestExecution execution) throws IOException {
		request.getHeaders().set(
				InternalRequestHeaders.INTERNAL_AUTHORIZATION,
				"Bearer " + this.tokenService.createToken(this.callerService));
		return execution.execute(request, body);
	}
}
