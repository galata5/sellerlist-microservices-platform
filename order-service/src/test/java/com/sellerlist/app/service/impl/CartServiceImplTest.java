package com.sellerlist.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sellerlist.app.client.ProductCatalogClient;
import com.sellerlist.app.domain.Cart;
import com.sellerlist.app.dto.CartMutationItemDto;
import com.sellerlist.app.dto.ProductCategorySnapshotDto;
import com.sellerlist.app.dto.ProductSnapshotDto;
import com.sellerlist.app.dto.UpdateCartRequest;
import com.sellerlist.app.repository.CartRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

	@Mock
	private CartRepository cartRepository;

	@Mock
	private ProductCatalogClient productCatalogClient;

	@InjectMocks
	private CartServiceImpl cartService;

	@Test
	void replaceCurrentCartUsesTrustedCatalogSnapshotInsteadOfClientPayload() {
		final Cart cart = Cart.builder()
				.cartId(15)
				.userId(9)
				.items(new LinkedHashSet<>())
				.build();
		when(cartRepository.findByUserId(9)).thenReturn(Optional.of(cart));
		when(cartRepository.save(cart)).thenReturn(cart);
		when(productCatalogClient.findProductById(7)).thenReturn(ProductSnapshotDto.builder()
				.productId(7)
				.productTitle("Trusted Product")
				.sku("SKU-7")
				.imageUrl("https://cdn.example.com/p-7.png")
				.priceUnit(49.99)
				.quantity(10)
				.categoryDto(ProductCategorySnapshotDto.builder()
						.categoryId(2)
						.categoryTitle("Outerwear")
						.build())
				.build());

		final var savedCart = cartService.replaceCurrentCart(9, UpdateCartRequest.builder()
				.items(new LinkedHashSet<>(java.util.Set.of(CartMutationItemDto.builder()
						.productId(7)
						.quantity(3)
						.build())))
				.build());

		assertThat(savedCart.getItems()).hasSize(1);
		final var savedItem = savedCart.getItems().iterator().next();
		assertThat(savedItem.getProductId()).isEqualTo(7);
		assertThat(savedItem.getProductTitle()).isEqualTo("Trusted Product");
		assertThat(savedItem.getPriceUnit()).isEqualTo(49.99);
		assertThat(savedItem.getQuantity()).isEqualTo(3);
		verify(productCatalogClient).findProductById(7);
		verify(cartRepository).save(cart);
	}
}
