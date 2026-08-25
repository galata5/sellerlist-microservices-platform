package com.sellerlist.app.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sellerlist.app.dto.CheckoutRequest;
import com.sellerlist.app.dto.OrderDto;
import com.sellerlist.app.dto.response.collection.DtoCollectionResponse;
import com.sellerlist.app.service.OrderService;
import com.sellerlist.platform.security.InternalRequestHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderResource {
	
	private final OrderService orderService;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<OrderDto>> findAll(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId) {
		log.info("*** OrderDto List, controller; fetch all orders *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderService.findAll(authenticatedUserId)));
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDto> findById(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@PathVariable("orderId") final Integer orderId) {
		log.info("*** OrderDto, resource; fetch order by id *");
		return ResponseEntity.ok(this.orderService.findById(authenticatedUserId, orderId));
	}
	
	@PostMapping
	public ResponseEntity<OrderDto> save() {
		throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Use /api/orders/checkout to create orders from the authenticated cart.");
	}

	@PostMapping("/checkout")
	public ResponseEntity<OrderDto> checkout(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@RequestBody
			@NotNull(message = "Input must not be NULL")
			@Valid final CheckoutRequest checkoutRequest) {
		log.info("*** OrderDto, resource; checkout current cart *");
		return ResponseEntity.ok(this.orderService.checkout(authenticatedUserId, checkoutRequest));
	}
	
	@PutMapping
	public ResponseEntity<OrderDto> update() {
		throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Orders are immutable through the public API after checkout.");
	}
	
	@PutMapping("/{orderId}")
	public ResponseEntity<OrderDto> update(@PathVariable("orderId") final Integer orderId) {
		throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Orders are immutable through the public API after checkout.");
	}
	
	@DeleteMapping("/{orderId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("orderId") final Integer orderId) {
		throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Orders cannot be deleted through the public API.");
	}
	
	
	
}






