package com.sellerlist.app.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartMutationItemDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Positive
	private Integer productId;

	@NotNull
	@Positive
	private Integer quantity;
}
