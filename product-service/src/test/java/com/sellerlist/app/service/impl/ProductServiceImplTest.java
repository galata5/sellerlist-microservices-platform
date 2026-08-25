package com.sellerlist.app.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.sellerlist.app.dto.ProductUpsertRequest;
import com.sellerlist.app.repository.CategoryRepository;
import com.sellerlist.app.repository.ProductRepository;

class ProductServiceImplTest {

	@Test
	void rejectsAPublishedSkuThatAlreadyExists() {
		final ProductRepository products = mock(ProductRepository.class);
		final CategoryRepository categories = mock(CategoryRepository.class);
		when(products.existsBySkuIgnoreCase("SELLERLIST-001")).thenReturn(true);
		final ProductServiceImpl service = new ProductServiceImpl(products, categories);
		final ProductUpsertRequest request = new ProductUpsertRequest(
				"Marketplace desk lamp",
				null,
				"sellerlist-001",
				new BigDecimal("49.99"),
				8,
				"Warm light with an adjustable arm.",
				new ProductUpsertRequest.CategoryReference(4));

		assertThatThrownBy(() -> service.publish(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("This SKU is already in use.");
	}
}
