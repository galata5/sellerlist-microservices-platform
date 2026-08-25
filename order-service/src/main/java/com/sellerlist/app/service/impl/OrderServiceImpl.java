package com.sellerlist.app.service.impl;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.sellerlist.app.domain.Cart;
import com.sellerlist.app.dto.CheckoutRequest;
import com.sellerlist.app.dto.OrderDto;
import com.sellerlist.app.exception.wrapper.OrderNotFoundException;
import com.sellerlist.app.helper.OrderMappingHelper;
import com.sellerlist.app.repository.CartRepository;
import com.sellerlist.app.messaging.OrderOutboxService;
import com.sellerlist.app.repository.OrderRepository;
import com.sellerlist.app.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	
	private final CartRepository cartRepository;
	private final OrderRepository orderRepository;
	private final OrderOutboxService orderOutboxService;
	
	@Override
	public List<OrderDto> findAll(final Integer authenticatedUserId) {
		log.info("*** OrderDto List, service; fetch all orders *");
		return this.orderRepository.findAllByUserId(authenticatedUserId)
				.stream()
					.map(OrderMappingHelper::map)
					.distinct()
					.collect(Collectors.toUnmodifiableList());
	}
	
	@Override
	public OrderDto findById(final Integer authenticatedUserId, final Integer orderId) {
		log.info("*** OrderDto, service; fetch order by id *");
		return this.orderRepository.findByOrderIdAndUserId(orderId, authenticatedUserId)
				.map(OrderMappingHelper::map)
				.orElseThrow(() -> new OrderNotFoundException(String
						.format("Order with id: %d not found", orderId)));
	}
	
	@Override
	public OrderDto save(final Integer authenticatedUserId, final OrderDto orderDto) {
		log.info("*** OrderDto, service; save order *");
		orderDto.setUserId(authenticatedUserId);
		if (orderDto.getCartDto() != null && orderDto.getCartDto().getCartId() != null) {
			final Cart ownedCart = this.cartRepository.findByCartIdAndUserId(orderDto.getCartDto().getCartId(), authenticatedUserId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Cart does not belong to the authenticated user."));
			orderDto.getCartDto().setUserId(ownedCart.getUserId());
		}
		final OrderDto savedOrder = OrderMappingHelper.map(this.orderRepository.save(OrderMappingHelper.map(orderDto)));
		orderOutboxService.enqueueOrderCreated(savedOrder);
		return savedOrder;
	}

	@Override
	public OrderDto checkout(final Integer authenticatedUserId, final CheckoutRequest request) {
		log.info("*** OrderDto, service; checkout cart for userId={} *", authenticatedUserId);
		final Cart cart = this.cartRepository.findByUserId(authenticatedUserId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No active cart was found for the authenticated user."));
		if (cart.getItems() == null || cart.getItems().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your cart is empty.");
		}

		final String generatedDescription = cart.getItems().stream()
				.map(item -> item.getProductTitle())
				.limit(3)
				.collect(Collectors.joining(", "));
		final Double total = cart.getItems().stream()
				.mapToDouble(item -> item.getPriceUnit() * item.getQuantity())
				.sum();
		final String deliverySummary = Stream.of(
				"Customer: " + request.getFullName(),
				"Phone: " + request.getPhoneNumber(),
				"Address: " + request.getStreetAddress() + ", " + request.getCity() + " " + request.getPostalCode(),
				"Payment: " + request.getPaymentMethod(),
				request.getEmail() != null && !request.getEmail().isBlank() ? "Email: " + request.getEmail() : null,
				request.getNotes() != null && !request.getNotes().isBlank() ? "Notes: " + request.getNotes() : null,
				"Items: " + generatedDescription)
				.filter(value -> value != null && !value.isBlank())
				.collect(Collectors.joining(" | "));

		final OrderDto orderDto = OrderDto.builder()
				.orderDate(java.time.LocalDateTime.now())
				.orderDesc(Stream.of(deliverySummary, request.getNotes(), generatedDescription)
						.filter(value -> value != null && !value.isBlank())
						.findFirst()
						.orElse("Checkout order"))
				.orderFee(total)
				.userId(authenticatedUserId)
				.cartDto(com.sellerlist.app.helper.CartMappingHelper.map(cart))
				.build();

		final OrderDto savedOrder = OrderMappingHelper.map(this.orderRepository.save(OrderMappingHelper.map(orderDto)));
		orderOutboxService.enqueueOrderCreated(savedOrder);
		cart.getItems().clear();
		this.cartRepository.save(cart);
		return savedOrder;
	}
	
	@Override
	public OrderDto update(final Integer authenticatedUserId, final OrderDto orderDto) {
		log.info("*** OrderDto, service; update order *");
		final OrderDto currentOrder = this.findById(authenticatedUserId, orderDto.getOrderId());
		orderDto.setUserId(currentOrder.getUserId());
		orderDto.setCartDto(currentOrder.getCartDto());
		return OrderMappingHelper.map(this.orderRepository
				.save(OrderMappingHelper.map(orderDto)));
	}
	
	@Override
	public OrderDto update(final Integer authenticatedUserId, final Integer orderId, final OrderDto orderDto) {
		log.info("*** OrderDto, service; update order with orderId *");
		final OrderDto currentOrder = this.findById(authenticatedUserId, orderId);
		orderDto.setOrderId(orderId);
		orderDto.setUserId(currentOrder.getUserId());
		orderDto.setCartDto(currentOrder.getCartDto());
		return OrderMappingHelper.map(this.orderRepository.save(OrderMappingHelper.map(orderDto)));
	}
	
	@Override
	public void deleteById(final Integer authenticatedUserId, final Integer orderId) {
		log.info("*** Void, service; delete order by id *");
		this.orderRepository.delete(OrderMappingHelper.map(this.findById(authenticatedUserId, orderId)));
	}
	
	
	
}



