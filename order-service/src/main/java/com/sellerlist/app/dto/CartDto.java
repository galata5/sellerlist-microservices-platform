package com.sellerlist.app.dto;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer cartId;
	private Integer userId;

	@Builder.Default
	@Valid
	private Set<CartItemDto> items = new LinkedHashSet<>();
	
	@JsonInclude(Include.NON_NULL)
	private Set<OrderDto> orderDtos;
	
	@JsonProperty("user")
	@JsonInclude(Include.NON_NULL)
	private UserDto userDto;
	
}









