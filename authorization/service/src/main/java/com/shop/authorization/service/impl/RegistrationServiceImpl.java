package com.shop.authorization.service.impl;

import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.dao.RoleRepository;
import com.shop.authorization.dao.UserDataRepository;
import com.shop.authorization.dto.model.UserDataDto;
import com.shop.authorization.dto.registration.RegistrationForm;
import com.shop.authorization.model.Role;
import com.shop.authorization.model.UserData;
import com.shop.authorization.service.RegistrationService;
import com.shop.authorization.service.encoder.PasswordEncoder;
import com.shop.authorization.service.exception.registration.EmailAlreadyExistsRegistrationException;
import com.shop.authorization.service.exception.registration.LoginAlreadyExistsRegistrationException;
import com.shop.authorization.service.exception.registration.PasswordsNotMatchRegistrationException;
import com.shop.authorization.service.exception.registration.UserSavingRegistrationException;
import com.shop.authorization.service.mapper.UserDataMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RegistrationServiceImpl implements RegistrationService {
    private final UserDataRepository userDataRepository;
    private final RoleRepository roleRepository;
    private final UserDataMapper userDataMapper;

    public RegistrationServiceImpl(UserDataRepository userDataRepository,
                                   RoleRepository roleRepository, UserDataMapper userDataMapper) {
        this.userDataRepository = userDataRepository;
        this.roleRepository = roleRepository;
        this.userDataMapper = userDataMapper;
    }

    @Override
    @Transactional
    public UserDataDto registerUser(@Valid RegistrationForm form) {
        checkIfLoginExists(form);
        checkIfEmailExists(form);
        checkIfPasswordMatch(form);
        UserData userData = saveUserIntoDatabase(form);
        return userDataMapper.mapToDto(userData);
    }

    private UserData saveUserIntoDatabase(RegistrationForm form) {
        try {
            String encodedPassword = PasswordEncoder.encode(form.getPassword());
            Role user_role = roleRepository.getByName(UsersRoles.USER);

            UserData userData = new UserData();
            userData.setLogin(form.getLogin());
            userData.setEmail(form.getEmail());
            userData.setPassword(encodedPassword);
            userData.setRoles(List.of(user_role));
            return userDataRepository.save(userData);
        } catch (Exception e) {
            log.error("Exception while savin user into database! Unable save user into database! {}", e.getMessage());
            throw new UserSavingRegistrationException(
                    String.format("Exception while savin user into database! Unable save user into database! %s",
                            e.getMessage())
            );
        }
    }

    private void checkIfPasswordMatch(RegistrationForm form) {
        if (!form.getPassword().equals(form.getRepeatPassword())) {
            log.warn("Passwords doesn't match!");
            throw new PasswordsNotMatchRegistrationException("Passwords doesn't match!");
        }
    }

    private void checkIfEmailExists(RegistrationForm form) {
        if (userDataRepository.existsByEmail(form.getEmail())) {
            log.warn("User with email '{}' already exists!", form.getEmail());
            throw new EmailAlreadyExistsRegistrationException(
                    String.format("User with email '%s' already exists!", form.getEmail())
            );
        }
    }

    private void checkIfLoginExists(RegistrationForm form) {
        if (userDataRepository.existsByLogin(form.getLogin())) {
            log.warn("User with login '{}' already exists!", form.getLogin());
            throw new LoginAlreadyExistsRegistrationException(
                    String.format("User with login '%s' already exists!", form.getLogin())
            );
        }
    }
}
