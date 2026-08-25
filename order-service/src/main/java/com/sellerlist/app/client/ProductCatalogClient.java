package com.sellerlist.app.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.sellerlist.app.dto.ProductSnapshotDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductCatalogClient {

	private final RestTemplate orderServiceRestTemplate;

	@Value("${app.catalog.base-url}")
	private String catalogBaseUrl;

	public ProductSnapshotDto findProductById(final Integer productId) {
		try {
			final ResponseEntity<ProductSnapshotDto> response = this.orderServiceRestTemplate.getForEntity(
					this.catalogBaseUrl + "/api/products/{productId}",
					ProductSnapshotDto.class,
					productId);
			if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is unavailable for checkout.");
			}
			return response.getBody();
		} catch (final RestClientException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Catalog lookup failed during cart validation.", exception);
		}
	}
}
