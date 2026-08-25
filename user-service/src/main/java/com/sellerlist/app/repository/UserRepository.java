package com.sellerlist.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByCredentialUsername(final String username);

	boolean existsByEmailIgnoreCase(final String email);
	
}
