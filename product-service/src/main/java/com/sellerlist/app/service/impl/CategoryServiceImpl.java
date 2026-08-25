package com.sellerlist.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellerlist.app.domain.Category;
import com.sellerlist.app.dto.CategoryDto;
import com.sellerlist.app.dto.CategoryUpsertRequest;
import com.sellerlist.app.exception.wrapper.CategoryNotFoundException;
import com.sellerlist.app.helper.CategoryMappingHelper;
import com.sellerlist.app.repository.CategoryRepository;
import com.sellerlist.app.service.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryServiceImpl(final CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryDto> browse() {
		return categoryRepository.findAllByVisibleTrueOrderByTitleAsc().stream()
				.map(CategoryMappingHelper::toDto)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CategoryDto get(final Integer categoryId) {
		return CategoryMappingHelper.toDto(load(categoryId));
	}

	@Override
	public CategoryDto create(final CategoryUpsertRequest request) {
		final String title = requiredText(request.categoryTitle(), "A category title is required.");
		categoryRepository.findByTitleIgnoreCase(title).ifPresent(category -> {
			throw new IllegalArgumentException("A category with this title already exists.");
		});
		final Category saved = categoryRepository.save(Category.create(title, optionalText(request.imageUrl()), parentOf(request.parentCategoryId())));
		return CategoryMappingHelper.toDto(saved);
	}

	@Override
	public CategoryDto update(final Integer categoryId, final CategoryUpsertRequest request) {
		final Category category = load(categoryId);
		final String title = requiredText(request.categoryTitle(), "A category title is required.");
		categoryRepository.findByTitleIgnoreCase(title)
				.filter(existing -> !existing.getCategoryId().equals(categoryId))
				.ifPresent(existing -> {
					throw new IllegalArgumentException("A category with this title already exists.");
				});
		final Category parent = parentOf(request.parentCategoryId());
		if (parent != null && parent.getCategoryId().equals(categoryId)) {
			throw new IllegalArgumentException("A category cannot be its own parent.");
		}
		category.revise(title, optionalText(request.imageUrl()), parent, request.visible() == null || request.visible());
		return CategoryMappingHelper.toDto(category);
	}

	@Override
	public void archive(final Integer categoryId) {
		final Category category = load(categoryId);
		if (categoryRepository.existsByParent_CategoryId(categoryId)) {
			throw new IllegalStateException("Move child categories before archiving this category.");
		}
		category.revise(category.getTitle(), category.getImageUrl(), category.getParent(), false);
	}

	private Category load(final Integer categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException(categoryId));
	}

	private Category parentOf(final Integer parentCategoryId) {
		return parentCategoryId == null ? null : load(parentCategoryId);
	}

	private static String requiredText(final String value, final String errorMessage) {
		final String trimmed = optionalText(value);
		if (trimmed == null) {
			throw new IllegalArgumentException(errorMessage);
		}
		return trimmed;
	}

	private static String optionalText(final String value) {
		if (value == null) {
			return null;
		}
		final String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
