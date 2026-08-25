package com.sellerlist.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpsertRequest(
		@NotBlank(message = "A category title is required.")
		@Size(max = 120, message = "A category title must contain at most 120 characters.")
		String categoryTitle,
		@Size(max = 500, message = "The image URL must contain at most 500 characters.")
		String imageUrl,
		Integer parentCategoryId,
		Boolean visible) {
}
