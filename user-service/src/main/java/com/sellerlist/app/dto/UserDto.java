package com.sellerlist.app.dto;

import java.io.Serializable;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer userId;
	
	@NotBlank
	@Size(max = 255)
	private String firstName;
	
	@NotBlank
	@Size(max = 255)
	private String lastName;
	
	@Size(max = 500)
	private String imageUrl;
	
	@NotBlank
	@Email
	@Size(max = 255)
	private String email;
	
	@Size(max = 50)
	private String phone;
	
	@JsonInclude(value = Include.NON_NULL)
	@Valid
	private Set<AddressDto> addressDtos;
	
	@JsonProperty("credential")
	@JsonInclude(value = Include.NON_NULL)
	@Valid
	private CredentialDto credentialDto;
	
}









