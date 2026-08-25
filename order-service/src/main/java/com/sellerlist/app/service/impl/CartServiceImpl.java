package com.sellerlist.app.service.impl;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sellerlist.app.client.ProductCatalogClient;
import com.sellerlist.app.dto.CartDto;
import com.sellerlist.app.dto.CartItemDto;
import com.sellerlist.app.dto.ProductSnapshotDto;
import com.sellerlist.app.dto.UpdateCartRequest;
import com.sellerlist.app.domain.Cart;
import com.sellerlist.app.helper.CartMappingHelper;
import com.sellerlist.app.repository.CartRepository;
import com.sellerlist.app.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	
	private final CartRepository cartRepository;
	private final ProductCatalogClient productCatalogClient;
	
	@Override
	public CartDto getCurrentCart(final Integer authenticatedUserId) {
		log.info("*** CartDto, service; fetch current cart for userId={} *", authenticatedUserId);
		return CartMappingHelper.map(this.findOrCreateCurrentCart(authenticatedUserId));
	}
	
	@Override
	public CartDto replaceCurrentCart(final Integer authenticatedUserId, final UpdateCartRequest updateCartRequest) {
		log.info("*** CartDto, service; replace current cart for userId={} *", authenticatedUserId);
		final Cart cart = this.findOrCreateCurrentCart(authenticatedUserId);
		cart.setUpdatedAt(Instant.now());
		cart.getItems().clear();
		this.cartRepository.saveAndFlush(cart);
		if (updateCartRequest.getItems() != null) {
			cart.getItems().addAll(this.toTrustedCartItems(updateCartRequest).stream()
					.map(item -> CartMappingHelper.mapItem(cart, item))
					.collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)));
		}
		return CartMappingHelper.map(this.cartRepository.save(cart));
	}
	
	@Override
	public void clearCurrentCart(final Integer authenticatedUserId) {
		log.info("*** Void, service; clear current cart for userId={} *", authenticatedUserId);
		final Cart cart = this.findOrCreateCurrentCart(authenticatedUserId);
		cart.getItems().clear();
		cart.setUpdatedAt(Instant.now());
		this.cartRepository.save(cart);
	}

	private Cart findOrCreateCurrentCart(final Integer authenticatedUserId) {
		return this.cartRepository.findByUserId(authenticatedUserId)
				.orElseGet(() -> this.cartRepository.save(Cart.builder()
						.userId(authenticatedUserId)
						.build()));
	}

	private Set<CartItemDto> toTrustedCartItems(final UpdateCartRequest updateCartRequest) {
		return updateCartRequest.getItems().stream()
				.map(item -> {
					final ProductSnapshotDto product = this.productCatalogClient.findProductById(item.getProductId());
					if (product.getQuantity() == null || product.getQuantity() < item.getQuantity()) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"Requested quantity exceeds currently available stock.");
					}
					return CartItemDto.builder()
							.productId(product.getProductId())
							.productTitle(product.getProductTitle())
							.sku(product.getSku())
							.imageUrl(product.getImageUrl())
							.categoryId(product.getCategoryDto() == null ? null : product.getCategoryDto().getCategoryId())
							.categoryTitle(product.getCategoryDto() == null ? null : product.getCategoryDto().getCategoryTitle())
							.priceUnit(product.getPriceUnit())
							.quantity(item.getQuantity())
							.build();
				})
				.collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
	}
	
	
	
}





