package com.sellerlist.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sellerlist.app.domain.Cart;
import com.sellerlist.app.domain.CartItem;
import com.sellerlist.app.domain.Order;
import com.sellerlist.app.dto.CheckoutRequest;
import com.sellerlist.app.repository.CartRepository;
import com.sellerlist.app.repository.OrderRepository;
import com.sellerlist.app.messaging.OrderOutboxService;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private CartRepository cartRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderOutboxService orderOutboxService;

	@InjectMocks
	private OrderServiceImpl orderService;

	@Test
	void checkoutUsesServerOwnedCartAndClearsItAfterSavingOrder() {
		final Cart cart = Cart.builder()
				.cartId(11)
				.userId(9)
				.items(new LinkedHashSet<>())
				.build();
		cart.getItems().add(CartItem.builder()
				.cart(cart)
				.productId(100)
				.productTitle("Textured Denim Jacket")
				.priceUnit(49.99)
				.quantity(2)
				.build());
		when(cartRepository.findByUserId(9)).thenReturn(Optional.of(cart));
		when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
			final Order order = invocation.getArgument(0, Order.class);
			order.setOrderId(77);
			return order;
		});

		final var savedOrder = orderService.checkout(9, CheckoutRequest.builder().notes("Leave at front desk").build());

		assertThat(savedOrder.getOrderId()).isEqualTo(77);
		assertThat(savedOrder.getOrderFee()).isEqualTo(99.98);
		assertThat(savedOrder.getUserId()).isEqualTo(9);
		assertThat(savedOrder.getCartDto()).isNotNull();
		assertThat(cart.getItems()).isEmpty();
		verify(cartRepository).save(cart);
		verify(orderOutboxService).enqueueOrderCreated(savedOrder);

		final ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		verify(orderRepository).save(orderCaptor.capture());
		assertThat(orderCaptor.getValue().getUserId()).isEqualTo(9);
		assertThat(orderCaptor.getValue().getCart().getCartId()).isEqualTo(11);
		assertThat(orderCaptor.getValue().getOrderDesc())
				.contains("Notes: Leave at front desk")
				.contains("Items: Textured Denim Jacket");
	}
}
