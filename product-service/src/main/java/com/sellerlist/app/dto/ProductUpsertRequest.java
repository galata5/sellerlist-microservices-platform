package com.sellerlist.app.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductUpsertRequest(
		@NotBlank(message = "A product title is required.")
		@Size(max = 180, message = "A product title must contain at most 180 characters.")
		String productTitle,
		@Size(max = 500, message = "The image URL must contain at most 500 characters.")
		String imageUrl,
		@NotBlank(message = "An SKU is required.")
		@Size(max = 80, message = "An SKU must contain at most 80 characters.")
		String sku,
		@NotNull(message = "A unit price is required.")
		@DecimalMin(value = "0.01", message = "The unit price must be greater than zero.")
		BigDecimal priceUnit,
		@NotNull(message = "A stock quantity is required.")
		@PositiveOrZero(message = "The stock quantity cannot be negative.")
		Integer quantity,
		@Size(max = 1200, message = "The description must contain at most 1200 characters.")
		String description,
		@NotNull(message = "A category is required.")
		@Valid
		CategoryReference category) {

	public record CategoryReference(
			@NotNull(message = "A category id is required.")
			Integer categoryId) {
	}
}
