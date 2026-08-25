package com.sellerlist.app.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellerlist.app.dto.CartDto;
import com.sellerlist.app.dto.UpdateCartRequest;
import com.sellerlist.app.service.CartService;
import com.sellerlist.platform.security.InternalRequestHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/carts")
@Slf4j
@RequiredArgsConstructor
public class CartResource {
	
	private final CartService cartService;
	
	@GetMapping("/me")
	public ResponseEntity<CartDto> findCurrentCart(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId) {
		log.info("*** CartDto, resource; fetch current cart *");
		return ResponseEntity.ok(this.cartService.getCurrentCart(authenticatedUserId));
	}
	
	@PutMapping("/me")
	public ResponseEntity<CartDto> replaceCurrentCart(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@RequestBody
			@NotNull(message = "Input must not be NULL")
			@Valid final UpdateCartRequest updateCartRequest) {
		log.info("*** CartDto, resource; replace current cart *");
		return ResponseEntity.ok(this.cartService.replaceCurrentCart(authenticatedUserId, updateCartRequest));
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Boolean> clearCurrentCart(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId) {
		log.info("*** Boolean, resource; clear current cart *");
		this.cartService.clearCurrentCart(authenticatedUserId);
		return ResponseEntity.ok(true);
	}
	
	
	
}







