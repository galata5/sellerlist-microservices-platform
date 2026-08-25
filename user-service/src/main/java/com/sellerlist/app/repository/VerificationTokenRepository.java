package com.sellerlist.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
	
	
	
}
