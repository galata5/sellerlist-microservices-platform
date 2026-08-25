package com.sellerlist.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {

	Optional<Cart> findByUserId(Integer userId);

	Optional<Cart> findByCartIdAndUserId(Integer cartId, Integer userId);
}
