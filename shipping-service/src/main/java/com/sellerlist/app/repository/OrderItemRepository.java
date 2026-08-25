package com.sellerlist.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.OrderItem;
import com.sellerlist.app.domain.id.OrderItemId;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
	
	
	
}
