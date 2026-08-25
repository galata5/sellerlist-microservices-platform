package com.sellerlist.app.service;

import java.util.List;

import com.sellerlist.app.dto.CheckoutRequest;
import com.sellerlist.app.dto.OrderDto;

public interface OrderService {
	
	List<OrderDto> findAll(final Integer authenticatedUserId);
	OrderDto findById(final Integer authenticatedUserId, final Integer orderId);
	OrderDto save(final Integer authenticatedUserId, final OrderDto orderDto);
	OrderDto checkout(final Integer authenticatedUserId, final CheckoutRequest request);
	OrderDto update(final Integer authenticatedUserId, final OrderDto orderDto);
	OrderDto update(final Integer authenticatedUserId, final Integer orderId, final OrderDto orderDto);
	void deleteById(final Integer authenticatedUserId, final Integer orderId);
	
}
