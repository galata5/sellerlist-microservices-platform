package com.sellerlist.app.resource;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sellerlist.app.dto.CategoryDto;
import com.sellerlist.app.dto.CategoryUpsertRequest;
import com.sellerlist.app.dto.response.collection.DtoCollectionResponse;
import com.sellerlist.app.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryResource {

	private final CategoryService categoryService;

	public CategoryResource(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public DtoCollectionResponse<CategoryDto> browse() {
		return new DtoCollectionResponse<>(categoryService.browse());
	}

	@GetMapping("/{categoryId}")
	public CategoryDto get(@PathVariable final Integer categoryId) {
		return categoryService.get(categoryId);
	}

	@PostMapping
	public ResponseEntity<CategoryDto> create(@Valid @RequestBody final CategoryUpsertRequest request) {
		final CategoryDto category = categoryService.create(request);
		final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{categoryId}")
				.buildAndExpand(category.categoryId())
				.toUri();
		return ResponseEntity.created(location).body(category);
	}

	@PutMapping("/{categoryId}")
	public CategoryDto update(
			@PathVariable final Integer categoryId,
			@Valid @RequestBody final CategoryUpsertRequest request) {
		return categoryService.update(categoryId, request);
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> archive(@PathVariable final Integer categoryId) {
		categoryService.archive(categoryId);
		return ResponseEntity.noContent().build();
	}
}
