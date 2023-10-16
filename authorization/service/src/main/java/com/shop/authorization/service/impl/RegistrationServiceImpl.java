package com.shop.authorization.service.impl;

import com.shop.authorization.dao.UserDataRepository;
import com.shop.authorization.dto.model.UserDataDto;
import com.shop.authorization.dto.registration.RegistrationForm;
import com.shop.authorization.service.RegistrationService;
import com.shop.authorization.service.exception.registration.EmailAlreadyExistsServiceException;
import com.shop.authorization.service.exception.registration.LoginAlreadyExistsServiceException;
import com.shop.authorization.service.exception.registration.PasswordsNotMatchServiceException;
import com.shop.authorization.service.mapper.UserDataMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RegistrationServiceImpl implements RegistrationService {

    private final UserDataRepository userDataRepository;
    private final UserDataMapper userDataMapper;

    public RegistrationServiceImpl(UserDataRepository userDataRepository,
                                   UserDataMapper userDataMapper) {
        this.userDataRepository = userDataRepository;
        this.userDataMapper = userDataMapper;
    }

    @Override
    @Transactional
    public UserDataDto registerUser(@Valid RegistrationForm form) {
        if (userDataRepository.existsByLogin(form.getLogin())) {
            log.warn("User with login '{}' already exists!", form.getLogin());
            throw new LoginAlreadyExistsServiceException(
                    String.format("User with login '%s' already exists!", form.getLogin())
            );
        }

        if (userDataRepository.existsByEmail(form.getEmail())) {
            log.warn("User with email '{}' already exists!", form.getEmail());
            throw new EmailAlreadyExistsServiceException(
                    String.format("User with email '%s' already exists!", form.getEmail())
            );
        }

        if (!form.getPassword().equals(form.getRepeatPassword())) {
            log.warn("Passwords doesn't match!");
            throw new PasswordsNotMatchServiceException("Passwords doesn't match!");
        }


    }
}
