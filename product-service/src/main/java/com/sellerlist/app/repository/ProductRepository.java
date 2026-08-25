package com.sellerlist.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findAllByListingStatusOrderByProductIdDesc(String listingStatus);

	boolean existsBySkuIgnoreCase(String sku);

	boolean existsBySkuIgnoreCaseAndProductIdNot(String sku, Integer productId);
}
