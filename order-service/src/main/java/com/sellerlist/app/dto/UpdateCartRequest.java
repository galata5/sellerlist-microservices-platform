package com.sellerlist.app.dto;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateCartRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@Builder.Default
	@Valid
	private Set<CartMutationItemDto> items = new LinkedHashSet<>();
}
