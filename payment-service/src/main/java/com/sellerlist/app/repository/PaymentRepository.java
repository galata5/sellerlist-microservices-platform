package com.sellerlist.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	
	Optional<Payment> findByOrderId(Integer orderId);
	Optional<Payment> findByOrderIdAndUserId(Integer orderId, Integer userId);
	Optional<Payment> findByPaymentIdAndUserId(Integer paymentId, Integer userId);
	List<Payment> findAllByUserId(Integer userId);
	
}
