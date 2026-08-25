package com.sellerlist.app.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sellerlist.app.dto.UserDto;
import com.sellerlist.app.domain.User;
import com.sellerlist.app.exception.wrapper.UserObjectNotFoundException;
import com.sellerlist.app.helper.UserMappingHelper;
import com.sellerlist.app.repository.UserRepository;
import com.sellerlist.app.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	
	@Override
	public List<UserDto> findAll() {
		log.info("*** UserDto List, service; fetch all users *");
		return this.userRepository.findAll()
				.stream()
					.map(UserMappingHelper::map)
					.distinct()
					.collect(Collectors.toUnmodifiableList());
	}
	
	@Override
	public UserDto findById(final Integer userId) {
		log.info("*** UserDto, service; fetch user by id *");
		return this.userRepository.findById(userId)
				.map(UserMappingHelper::map)
				.orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
	}
	
	@Override
	public UserDto save(final UserDto userDto) {
		log.info("*** UserDto, service; save user *");
		return UserMappingHelper.map(this.userRepository.save(this.prepareForWrite(UserMappingHelper.map(userDto), true)));
	}
	
	@Override
	public UserDto update(final UserDto userDto) {
		log.info("*** UserDto, service; update user *");
		return UserMappingHelper.map(this.userRepository.save(this.prepareForWrite(UserMappingHelper.map(userDto), false)));
	}
	
	@Override
	public UserDto update(final Integer userId, final UserDto userDto) {
		log.info("*** UserDto, service; update user with userId *");
		this.findById(userId);
		userDto.setUserId(userId);
		return UserMappingHelper.map(this.userRepository.save(this.prepareForWrite(UserMappingHelper.map(userDto), false)));
	}
	
	@Override
	public void deleteById(final Integer userId) {
		log.info("*** Void, service; delete user by id *");
		this.userRepository.deleteById(userId);
	}
	
	@Override
	public UserDto findByUsername(final String username) {
		log.info("*** UserDto, service; fetch user with username *");
		return UserMappingHelper.map(this.userRepository.findByCredentialUsername(username)
				.orElseThrow(() -> new UserObjectNotFoundException(String.format("User with username: %s not found", username))));
	}

	private User prepareForWrite(final User user, final boolean isCreate) {
		final Instant now = Instant.now();
		if (isCreate && user.getCreatedAt() == null) {
			user.setCreatedAt(now);
		}
		user.setUpdatedAt(now);
		if (user.getCredential() != null) {
			user.getCredential().setUser(user);
			if (isCreate && user.getCredential().getCreatedAt() == null) {
				user.getCredential().setCreatedAt(now);
			}
			user.getCredential().setUpdatedAt(now);
		}
		return user;
	}
	
	
	
}








