package com.sellerlist.app.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellerlist.app.domain.Category;
import com.sellerlist.app.domain.Product;
import com.sellerlist.app.dto.ProductDto;
import com.sellerlist.app.dto.ProductUpsertRequest;
import com.sellerlist.app.exception.wrapper.CategoryNotFoundException;
import com.sellerlist.app.exception.wrapper.ProductNotFoundException;
import com.sellerlist.app.helper.ProductMappingHelper;
import com.sellerlist.app.repository.CategoryRepository;
import com.sellerlist.app.repository.ProductRepository;
import com.sellerlist.app.service.ProductService;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductServiceImpl(final ProductRepository productRepository, final CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> browse() {
		return productRepository.findAllByListingStatusOrderByProductIdDesc("ACTIVE").stream()
				.map(ProductMappingHelper::toDto)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDto get(final Integer productId) {
		return ProductMappingHelper.toDto(loadActive(productId));
	}

	@Override
	public ProductDto publish(final ProductUpsertRequest request) {
		final String sku = normaliseSku(request.sku());
		if (productRepository.existsBySkuIgnoreCase(sku)) {
			throw new IllegalArgumentException("This SKU is already in use.");
		}
		final Product product = Product.publish(
				requiredText(request.productTitle(), "A product title is required."),
				optionalText(request.imageUrl()),
				sku,
				priceOf(request.priceUnit()),
				quantityOf(request.quantity()),
				optionalText(request.description()),
				loadVisibleCategory(request.category().categoryId()));
		return ProductMappingHelper.toDto(productRepository.save(product));
	}

	@Override
	public ProductDto update(final Integer productId, final ProductUpsertRequest request) {
		final Product product = loadActive(productId);
		final String sku = normaliseSku(request.sku());
		if (productRepository.existsBySkuIgnoreCaseAndProductIdNot(sku, productId)) {
			throw new IllegalArgumentException("This SKU is already in use.");
		}
		product.revise(
				requiredText(request.productTitle(), "A product title is required."),
				optionalText(request.imageUrl()),
				sku,
				priceOf(request.priceUnit()),
				quantityOf(request.quantity()),
				optionalText(request.description()),
				loadVisibleCategory(request.category().categoryId()));
		return ProductMappingHelper.toDto(product);
	}

	@Override
	public void withdraw(final Integer productId) {
		loadActive(productId).withdraw();
	}

	private Product loadActive(final Integer productId) {
		final Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));
		if (!product.isActive()) {
			throw new ProductNotFoundException(productId);
		}
		return product;
	}

	private Category loadVisibleCategory(final Integer categoryId) {
		final Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		if (!category.isVisible()) {
			throw new IllegalArgumentException("Products can only be listed in a visible category.");
		}
		return category;
	}

	private static String normaliseSku(final String sku) {
		return requiredText(sku, "An SKU is required.").toUpperCase(Locale.ROOT).replaceAll("\\s+", "-");
	}

	private static BigDecimal priceOf(final BigDecimal price) {
		if (price == null || price.signum() <= 0) {
			throw new IllegalArgumentException("The unit price must be greater than zero.");
		}
		return price.setScale(2, RoundingMode.HALF_UP);
	}

	private static int quantityOf(final Integer quantity) {
		if (quantity == null || quantity < 0) {
			throw new IllegalArgumentException("The stock quantity cannot be negative.");
		}
		return quantity;
	}

	private static String requiredText(final String value, final String errorMessage) {
		final String trimmed = optionalText(value);
		if (trimmed == null) {
			throw new IllegalArgumentException(errorMessage);
		}
		return trimmed;
	}

	private static String optionalText(final String value) {
		if (value == null) {
			return null;
		}
		final String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
