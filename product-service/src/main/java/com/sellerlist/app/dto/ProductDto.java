package com.sellerlist.app.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductDto(
		Integer productId,
		String productTitle,
		String imageUrl,
		String sku,
		BigDecimal priceUnit,
		int quantity,
		String description,
		CategoryDto category) {
}
