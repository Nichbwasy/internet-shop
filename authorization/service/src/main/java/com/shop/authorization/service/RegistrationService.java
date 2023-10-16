package com.shop.authorization.service;

import com.shop.authorization.dto.model.UserDataDto;
import com.shop.authorization.dto.registration.RegistrationForm;
import jakarta.validation.Valid;

public interface RegistrationService {

    UserDataDto registerUser(@Valid RegistrationForm form);

}
