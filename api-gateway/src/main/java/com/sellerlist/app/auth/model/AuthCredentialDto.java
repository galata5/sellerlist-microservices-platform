package com.sellerlist.app.auth.model;

public record AuthCredentialDto(
	Integer credentialId,
	Integer userId,
	String username,
	String password,
	RoleBasedAuthority roleBasedAuthority,
	Boolean isEnabled,
	Boolean isAccountNonExpired,
	Boolean isAccountNonLocked,
	Boolean isCredentialsNonExpired) {
}
