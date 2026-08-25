package com.sellerlist.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryDto(
		Integer categoryId,
		String categoryTitle,
		String imageUrl,
		CategoryDto parentCategory) {
}
