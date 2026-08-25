package com.sellerlist.app.helper;

import com.sellerlist.app.domain.Credential;
import com.sellerlist.app.domain.User;
import com.sellerlist.app.dto.CredentialDto;
import com.sellerlist.app.dto.UserDto;

public interface UserMappingHelper {
	
	public static UserDto map(final User user) {
		final CredentialDto credentialDto = user.getCredential() == null
			? null
			: CredentialDto.builder()
				.credentialId(user.getCredential().getCredentialId())
				.username(user.getCredential().getUsername())
				.roleBasedAuthority(user.getCredential().getRoleBasedAuthority())
				.isEnabled(user.getCredential().getIsEnabled())
				.isAccountNonExpired(user.getCredential().getIsAccountNonExpired())
				.isAccountNonLocked(user.getCredential().getIsAccountNonLocked())
				.isCredentialsNonExpired(user.getCredential().getIsCredentialsNonExpired())
				.build();

		return UserDto.builder()
				.userId(user.getUserId())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.imageUrl(user.getImageUrl())
				.email(user.getEmail())
				.phone(user.getPhone())
				.credentialDto(credentialDto)
				.build();
	}
	
	public static User map(final UserDto userDto) {
		final Credential credential = userDto.getCredentialDto() == null
			? null
			: Credential.builder()
				.credentialId(userDto.getCredentialDto().getCredentialId())
				.username(userDto.getCredentialDto().getUsername())
				.password(userDto.getCredentialDto().getPassword())
				.roleBasedAuthority(userDto.getCredentialDto().getRoleBasedAuthority())
				.isEnabled(userDto.getCredentialDto().getIsEnabled())
				.isAccountNonExpired(userDto.getCredentialDto().getIsAccountNonExpired())
				.isAccountNonLocked(userDto.getCredentialDto().getIsAccountNonLocked())
				.isCredentialsNonExpired(userDto.getCredentialDto().getIsCredentialsNonExpired())
				.build();

		return User.builder()
				.userId(userDto.getUserId())
				.firstName(userDto.getFirstName())
				.lastName(userDto.getLastName())
				.imageUrl(userDto.getImageUrl())
				.email(userDto.getEmail())
				.phone(userDto.getPhone())
				.credential(credential)
				.build();
	}
	
	
	
}




