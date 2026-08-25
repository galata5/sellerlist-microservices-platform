package com.sellerlist.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {
	
	
	
}
