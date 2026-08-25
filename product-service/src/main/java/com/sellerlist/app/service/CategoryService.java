package com.sellerlist.app.service;

import java.util.List;

import com.sellerlist.app.dto.CategoryDto;
import com.sellerlist.app.dto.CategoryUpsertRequest;

public interface CategoryService {

	List<CategoryDto> browse();

	CategoryDto get(Integer categoryId);

	CategoryDto create(CategoryUpsertRequest request);

	CategoryDto update(Integer categoryId, CategoryUpsertRequest request);

	void archive(Integer categoryId);
}
