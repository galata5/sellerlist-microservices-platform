package com.sellerlist.app.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CheckoutRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(max = 120)
	private String fullName;

	@NotBlank
	@Size(max = 40)
	private String phoneNumber;

	@NotBlank
	@Size(max = 120)
	private String city;

	@NotBlank
	@Size(max = 200)
	private String streetAddress;

	@NotBlank
	@Size(max = 30)
	private String postalCode;

	@Email
	@Size(max = 160)
	private String email;

	@NotBlank
	@Size(max = 40)
	private String paymentMethod;

	@Size(max = 255)
	private String notes;
}
