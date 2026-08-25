package com.sellerlist.app.service.impl;

import java.time.Instant;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sellerlist.app.domain.Credential;
import com.sellerlist.app.domain.RoleBasedAuthority;
import com.sellerlist.app.domain.User;
import com.sellerlist.app.dto.RegistrationRequest;
import com.sellerlist.app.dto.UserDto;
import com.sellerlist.app.helper.UserMappingHelper;
import com.sellerlist.app.repository.CredentialRepository;
import com.sellerlist.app.repository.UserRepository;
import com.sellerlist.app.service.RegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	private final UserRepository userRepository;
	private final CredentialRepository credentialRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDto register(final RegistrationRequest registrationRequest) {
		if (this.credentialRepository.existsByUsernameIgnoreCase(registrationRequest.username())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered.");
		}
		if (this.userRepository.existsByEmailIgnoreCase(registrationRequest.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email address is already registered.");
		}

		final Instant now = Instant.now();
		final User user = User.builder()
			.firstName(registrationRequest.firstName().trim())
			.lastName(registrationRequest.lastName().trim())
			.email(registrationRequest.email().trim().toLowerCase())
			.phone(registrationRequest.phone().trim())
			.build();
		user.setCreatedAt(now);
		user.setUpdatedAt(now);

		final Credential credential = Credential.builder()
			.username(registrationRequest.username().trim())
			.password(this.passwordEncoder.encode(registrationRequest.password()))
			.roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
			.isEnabled(true)
			.isAccountNonExpired(true)
			.isAccountNonLocked(true)
			.isCredentialsNonExpired(true)
			.user(user)
			.build();
		credential.setCreatedAt(now);
		credential.setUpdatedAt(now);
		user.setCredential(credential);

		log.info("*** User registration, service; create a new marketplace account *");
		final UserDto registeredUser = UserMappingHelper.map(this.userRepository.save(user));
		registeredUser.setCredentialDto(null);
		return registeredUser;
	}
}
