package com.sellerlist.app.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
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

import com.sellerlist.app.dto.RegistrationRequest;
import com.sellerlist.app.dto.UserDto;
import com.sellerlist.app.dto.response.collection.DtoCollectionResponse;
import com.sellerlist.app.service.RegistrationService;
import com.sellerlist.app.service.UserService;
import com.sellerlist.platform.security.InternalRequestHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = {"/api/users"})
@Slf4j
@RequiredArgsConstructor
public class UserResource {
	
	private final UserService userService;
	private final RegistrationService registrationService;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<UserDto>> findAll(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId) {
		log.info("*** UserDto List, controller; fetch all users *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(java.util.List.of(this.userService.findById(authenticatedUserId))));
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> findById(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@PathVariable("userId") final Integer userId) {
		log.info("*** UserDto, resource; fetch user by id *");
		requireOwnership(authenticatedUserId, userId);
		return ResponseEntity.ok(this.userService.findById(userId));
	}
	
	@PostMapping
	public ResponseEntity<UserDto> save() {
		throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Use /api/users/register to create user accounts.");
	}

	@PostMapping("/register")
	public ResponseEntity<UserDto> register(
			@RequestBody
			@NotNull(message = "Input must not NULL")
			@Valid final RegistrationRequest registrationRequest) {
		log.info("*** UserDto, resource; register a new user account *");
		return ResponseEntity.status(201).body(this.registrationService.register(registrationRequest));
	}
	
	@PutMapping
	public ResponseEntity<UserDto> update(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@RequestBody 
			@NotNull(message = "Input must not NULL") 
			@Valid final UserDto userDto) {
		log.info("*** UserDto, resource; update user *");
		userDto.setUserId(authenticatedUserId);
		return ResponseEntity.ok(this.userService.update(authenticatedUserId, userDto));
	}
	
	@PutMapping("/{userId}")
	public ResponseEntity<UserDto> update(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@PathVariable("userId") final Integer userId, 
			@RequestBody 
			@NotNull(message = "Input must not NULL") 
			@Valid final UserDto userDto) {
		log.info("*** UserDto, resource; update user with userId *");
		requireOwnership(authenticatedUserId, userId);
		return ResponseEntity.ok(this.userService.update(userId, userDto));
	}
	
	@DeleteMapping("/{userId}")
	public ResponseEntity<Boolean> deleteById(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USER_ID) final Integer authenticatedUserId,
			@PathVariable("userId") final Integer userId) {
		log.info("*** Boolean, resource; delete user by id *");
		requireOwnership(authenticatedUserId, userId);
		this.userService.deleteById(userId);
		return ResponseEntity.ok(true);
	}
	
	@GetMapping("/username/{username}")
	public ResponseEntity<UserDto> findByUsername(
			@RequestHeader(InternalRequestHeaders.AUTHENTICATED_USERNAME) final String authenticatedUsername,
			@PathVariable("username") final String username) {
		if (!authenticatedUsername.equalsIgnoreCase(username.strip())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own user profile.");
		}
		return ResponseEntity.ok(this.userService.findByUsername(username));
	}

	private void requireOwnership(final Integer authenticatedUserId, final Integer requestedUserId) {
		if (!authenticatedUserId.equals(requestedUserId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own user profile.");
		}
	}
	
	
	
}







