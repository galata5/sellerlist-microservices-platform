package com.sellerlist.app.helper;

import com.sellerlist.app.domain.Product;
import com.sellerlist.app.dto.ProductDto;

public final class ProductMappingHelper {

	private ProductMappingHelper() {
	}

	public static ProductDto toDto(final Product product) {
		return new ProductDto(
				product.getProductId(),
				product.getTitle(),
				product.getImageUrl(),
				product.getSku(),
				product.getUnitPrice(),
				product.getStockOnHand(),
				product.getDescription(),
				CategoryMappingHelper.toDto(product.getCategory()));
	}
}
