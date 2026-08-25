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

import com.sellerlist.app.dto.ProductDto;
import com.sellerlist.app.dto.ProductUpsertRequest;
import com.sellerlist.app.dto.response.collection.DtoCollectionResponse;
import com.sellerlist.app.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductResource {

	private final ProductService productService;

	public ProductResource(final ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public DtoCollectionResponse<ProductDto> browse() {
		return new DtoCollectionResponse<>(productService.browse());
	}

	@GetMapping("/{productId}")
	public ProductDto get(@PathVariable final Integer productId) {
		return productService.get(productId);
	}

	@PostMapping
	public ResponseEntity<ProductDto> publish(@Valid @RequestBody final ProductUpsertRequest request) {
		final ProductDto product = productService.publish(request);
		final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{productId}")
				.buildAndExpand(product.productId())
				.toUri();
		return ResponseEntity.created(location).body(product);
	}

	@PutMapping("/{productId}")
	public ProductDto update(
			@PathVariable final Integer productId,
			@Valid @RequestBody final ProductUpsertRequest request) {
		return productService.update(productId, request);
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<Void> withdraw(@PathVariable final Integer productId) {
		productService.withdraw(productId);
		return ResponseEntity.noContent().build();
	}
}
