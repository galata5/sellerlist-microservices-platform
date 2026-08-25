package com.sellerlist.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellerlist.app.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findAllByVisibleTrueOrderByTitleAsc();

	Optional<Category> findByTitleIgnoreCase(String title);

	boolean existsByParent_CategoryId(Integer parentCategoryId);
}
