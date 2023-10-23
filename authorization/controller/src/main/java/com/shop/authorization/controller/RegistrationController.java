package com.shop.authorization.controller;

import com.shop.authorization.dto.model.UserDataDto;
import com.shop.authorization.dto.registration.RegistrationForm;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService  registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<AccessRefreshTokens> registerUser(@ModelAttribute RegistrationForm form) {
        log.info("Trying to register a new user...");
        return ResponseEntity.ok().body(registrationService.registerUser(form));
    }

}
