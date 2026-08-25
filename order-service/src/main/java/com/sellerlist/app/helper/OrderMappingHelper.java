package com.sellerlist.app.helper;

import com.sellerlist.app.domain.Cart;
import com.sellerlist.app.domain.Order;
import com.sellerlist.app.dto.CartDto;
import com.sellerlist.app.dto.OrderDto;

public interface OrderMappingHelper {
	
	public static OrderDto map(final Order order) {
		final Cart cart = order.getCart();
		return OrderDto.builder()
				.orderId(order.getOrderId())
				.orderDate(order.getOrderDate())
				.orderDesc(order.getOrderDesc())
				.orderFee(order.getOrderFee())
				.userId(order.getUserId())
				.cartDto(cart == null
						? null
						: CartDto.builder()
								.cartId(cart.getCartId())
								.userId(cart.getUserId())
								.build())
				.build();
	}
	
	public static Order map(final OrderDto orderDto) {
		return Order.builder()
				.orderId(orderDto.getOrderId())
				.orderDate(orderDto.getOrderDate())
				.orderDesc(orderDto.getOrderDesc())
				.orderFee(orderDto.getOrderFee())
				.userId(orderDto.getUserId())
				.cart(orderDto.getCartDto() == null
						? null
						: Cart.builder()
								.cartId(orderDto.getCartDto().getCartId())
								.build())
				.build();
	}
	
	
	
}







