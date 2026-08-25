package com.sellerlist.app.dto;

import java.io.Serializable;

import com.sellerlist.app.domain.RoleBasedAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AuthCredentialDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer credentialId;
	private Integer userId;
	private String username;
	private String password;
	private RoleBasedAuthority roleBasedAuthority;
	private Boolean isEnabled;
	private Boolean isAccountNonExpired;
	private Boolean isAccountNonLocked;
	private Boolean isCredentialsNonExpired;
	
}
