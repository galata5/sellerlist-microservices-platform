package com.sellerlist.app.service;

import java.util.List;

import com.sellerlist.app.dto.PaymentDto;
import com.sellerlist.platform.events.OrderCreatedEvent;

public interface PaymentService {
	
	List<PaymentDto> findAll(final Integer authenticatedUserId);
	PaymentDto findById(final Integer authenticatedUserId, final Integer paymentId);
	PaymentDto findByOrderId(final Integer authenticatedUserId, final Integer orderId);
	PaymentDto save(final Integer authenticatedUserId, final PaymentDto paymentDto);
	PaymentDto update(final Integer authenticatedUserId, final PaymentDto paymentDto);
	PaymentDto provisionForOrder(final Integer orderId, final Integer userId);
	void handleOrderCreated(final OrderCreatedEvent event, final String consumerName);
	void deleteById(final Integer authenticatedUserId, final Integer paymentId);
	
}
