package com.sellerlist.app.helper;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import com.sellerlist.app.domain.Cart;
import com.sellerlist.app.domain.CartItem;
import com.sellerlist.app.dto.CartDto;
import com.sellerlist.app.dto.CartItemDto;
import com.sellerlist.app.dto.UserDto;

public interface CartMappingHelper {
	
	public static CartDto map(final Cart cart) {
		return CartDto.builder()
				.cartId(cart.getCartId())
				.userId(cart.getUserId())
				.items(cart.getItems() == null
						? new LinkedHashSet<>()
						: cart.getItems().stream()
								.map(CartMappingHelper::mapItem)
								.collect(Collectors.toCollection(LinkedHashSet::new)))
				.userDto(
						UserDto.builder()
							.userId(cart.getUserId())
							.build())
				.build();
	}
	
	public static Cart map(final CartDto cartDto) {
		final Cart cart = Cart.builder()
				.cartId(cartDto.getCartId())
				.userId(cartDto.getUserId())
				.build();
		if (cartDto.getItems() != null) {
			cart.setItems(cartDto.getItems().stream()
					.map(item -> mapItem(cart, item))
					.collect(Collectors.toCollection(LinkedHashSet::new)));
		}
		return cart;
	}

	public static CartItemDto mapItem(final CartItem cartItem) {
		return CartItemDto.builder()
				.cartItemId(cartItem.getCartItemId())
				.productId(cartItem.getProductId())
				.productTitle(cartItem.getProductTitle())
				.sku(cartItem.getSku())
				.imageUrl(cartItem.getImageUrl())
				.categoryId(cartItem.getCategoryId())
				.categoryTitle(cartItem.getCategoryTitle())
				.priceUnit(cartItem.getPriceUnit())
				.quantity(cartItem.getQuantity())
				.build();
	}

	public static CartItem mapItem(final Cart cart, final CartItemDto cartItemDto) {
		return CartItem.builder()
				.cartItemId(cartItemDto.getCartItemId())
				.cart(cart)
				.productId(cartItemDto.getProductId())
				.productTitle(cartItemDto.getProductTitle())
				.sku(cartItemDto.getSku())
				.imageUrl(cartItemDto.getImageUrl())
				.categoryId(cartItemDto.getCategoryId())
				.categoryTitle(cartItemDto.getCategoryTitle())
				.priceUnit(cartItemDto.getPriceUnit())
				.quantity(cartItemDto.getQuantity())
				.build();
	}
	
	
	
}









