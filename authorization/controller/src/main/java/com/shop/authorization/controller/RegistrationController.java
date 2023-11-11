package com.shop.authorization.controller;

import com.shop.authorization.dto.model.UserDataDto;
import com.shop.authorization.dto.registration.RegistrationForm;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService  registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<AccessRefreshTokens> registerUser(@Valid @RequestBody RegistrationForm form) {
        log.info("Trying to register a new user...");
        return ResponseEntity.ok().body(registrationService.registerUser(form));
    }

}
