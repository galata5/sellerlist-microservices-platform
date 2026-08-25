package com.sellerlist.app.dto;

import java.io.Serializable;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
		@NotBlank(message = "First name is required.")
		@Size(max = 80, message = "First name must not exceed 80 characters.")
		String firstName,
		@NotBlank(message = "Last name is required.")
		@Size(max = 80, message = "Last name must not exceed 80 characters.")
		String lastName,
		@NotBlank(message = "Email is required.")
		@Email(message = "Email must be valid.")
		String email,
		@NotBlank(message = "Phone number is required.")
		@Pattern(regexp = "^[0-9+()\\-\\s]{8,20}$", message = "Phone number format is invalid.")
		String phone,
		@NotBlank(message = "Username is required.")
		@Pattern(regexp = "^[A-Za-z0-9._-]{4,32}$", message = "Username must be 4-32 characters and contain only letters, numbers, dots, underscores, or hyphens.")
		String username,
		@NotBlank(message = "Password is required.")
		@Pattern(
				regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{12,72}$",
				message = "Password must be 12-72 characters long and include upper, lower, number, and special characters.")
		String password,
		String company) implements Serializable {

	private static final long serialVersionUID = 1L;

	@AssertTrue(message = "Registration validation failed.")
	public boolean isBotTrapClear() {
		return this.company == null || this.company.isBlank();
	}
}
