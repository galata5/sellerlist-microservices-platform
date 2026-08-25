package com.sellerlist.app.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartItemDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer cartItemId;

	@NotNull
	@Positive
	private Integer productId;

	@NotBlank
	@Size(max = 255)
	private String productTitle;

	@Size(max = 255)
	private String sku;

	@Size(max = 500)
	private String imageUrl;

	private Integer categoryId;

	@Size(max = 255)
	private String categoryTitle;

	@NotNull
	@Positive
	private Double priceUnit;

	@NotNull
	@Positive
	private Integer quantity;
}
