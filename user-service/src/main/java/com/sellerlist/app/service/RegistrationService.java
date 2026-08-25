package com.sellerlist.app.service;

import com.sellerlist.app.dto.RegistrationRequest;
import com.sellerlist.app.dto.UserDto;

public interface RegistrationService {

	UserDto register(RegistrationRequest registrationRequest);
}
