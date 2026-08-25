package com.sellerlist.app.helper;

import com.sellerlist.app.domain.Category;
import com.sellerlist.app.dto.CategoryDto;

public final class CategoryMappingHelper {

	private CategoryMappingHelper() {
	}

	public static CategoryDto toDto(final Category category) {
		if (category == null) {
			return null;
		}

		return new CategoryDto(
				category.getCategoryId(),
				category.getTitle(),
				category.getImageUrl(),
				toSummary(category.getParent()));
	}

	private static CategoryDto toSummary(final Category category) {
		if (category == null) {
			return null;
		}
		return new CategoryDto(category.getCategoryId(), category.getTitle(), category.getImageUrl(), null);
	}
}
