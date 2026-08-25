package com.sellerlist.app.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sellerlist.app.dto.AuthCredentialDto;
import com.sellerlist.app.dto.CredentialDto;
import com.sellerlist.app.exception.wrapper.CredentialNotFoundException;
import com.sellerlist.app.exception.wrapper.UserObjectNotFoundException;
import com.sellerlist.app.helper.CredentialMappingHelper;
import com.sellerlist.app.repository.CredentialRepository;
import com.sellerlist.app.service.CredentialService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {
	
	private final CredentialRepository credentialRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public List<CredentialDto> findAll() {
		log.info("*** CredentialDto List, service; fetch all credentials *");
		return this.credentialRepository.findAll()
				.stream()
					.map(CredentialMappingHelper::map)
					.distinct()
					.collect(Collectors.toUnmodifiableList());
	}
	
	@Override
	public CredentialDto findById(final Integer credentialId) {
		log.info("*** CredentialDto, service; fetch credential by ids *");
		return this.credentialRepository.findById(credentialId)
				.map(CredentialMappingHelper::map)
				.orElseThrow(() -> new CredentialNotFoundException(String.format("#### Credential with id: %d not found! ####", credentialId)));
	}
	
	@Override
	public CredentialDto save(final CredentialDto credentialDto) {
		log.info("*** CredentialDto, service; save credential *");
		final var credential = CredentialMappingHelper.map(credentialDto);
		credential.setPassword(this.passwordEncoder.encode(credentialDto.getPassword()));
		return CredentialMappingHelper.map(this.credentialRepository.save(this.prepareForWrite(credential, true)));
	}
	
	@Override
	public CredentialDto update(final CredentialDto credentialDto) {
		log.info("*** CredentialDto, service; update credential *");
		final var current = this.credentialRepository.findById(credentialDto.getCredentialId())
				.orElseThrow(() -> new CredentialNotFoundException(String.format("Credential with id: %d not found", credentialDto.getCredentialId())));
		final var credential = CredentialMappingHelper.map(credentialDto);
		if (credentialDto.getPassword() == null || credentialDto.getPassword().isBlank()) {
			credential.setPassword(current.getPassword());
		} else {
			credential.setPassword(this.passwordEncoder.encode(credentialDto.getPassword()));
		}
		return CredentialMappingHelper.map(this.credentialRepository.save(this.prepareForWrite(credential, false)));
	}
	
	@Override
	public CredentialDto update(final Integer credentialId, final CredentialDto credentialDto) {
		log.info("*** CredentialDto, service; update credential with credentialId *");
		this.findById(credentialId);
		credentialDto.setCredentialId(credentialId);
		return this.update(credentialDto);
	}
	
	@Override
	public void deleteById(final Integer credentialId) {
		log.info("*** Void, service; delete credential by id *");
		this.credentialRepository.deleteById(credentialId);
	}
	
	@Override
	public CredentialDto findByUsername(final String username) {
		return CredentialMappingHelper.map(this.credentialRepository.findByUsername(username)
				.orElseThrow(() -> new UserObjectNotFoundException(String.format("#### Credential with username: %s not found! ####", username))));
	}

	@Override
	public AuthCredentialDto findAuthByUsername(final String username) {
		return this.credentialRepository.findByUsername(username)
				.map(CredentialMappingHelper::mapAuth)
				.orElseThrow(() -> new UserObjectNotFoundException(String.format("#### Credential with username: %s not found! ####", username)));
	}

	private com.sellerlist.app.domain.Credential prepareForWrite(
			final com.sellerlist.app.domain.Credential credential,
			final boolean isCreate) {
		final Instant now = Instant.now();
		if (isCreate && credential.getCreatedAt() == null) {
			credential.setCreatedAt(now);
		}
		credential.setUpdatedAt(now);
		if (credential.getUser() != null) {
			credential.getUser().setCredential(credential);
			if (isCreate && credential.getUser().getCreatedAt() == null) {
				credential.getUser().setCreatedAt(now);
			}
			credential.getUser().setUpdatedAt(now);
		}
		return credential;
	}
	
	
	
}








