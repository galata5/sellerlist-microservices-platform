package com.sellerlist.app.service;

import java.util.List;

import com.sellerlist.app.dto.ProductDto;
import com.sellerlist.app.dto.ProductUpsertRequest;

public interface ProductService {

	List<ProductDto> browse();

	ProductDto get(Integer productId);

	ProductDto publish(ProductUpsertRequest request);

	ProductDto update(Integer productId, ProductUpsertRequest request);

	void withdraw(Integer productId);
}
