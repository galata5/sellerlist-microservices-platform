package com.sellerlist.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findAllByUserId(Integer userId);

	Optional<Order> findByOrderIdAndUserId(Integer orderId, Integer userId);
}
