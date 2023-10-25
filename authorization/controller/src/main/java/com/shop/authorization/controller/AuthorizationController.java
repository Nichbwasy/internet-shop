package com.shop.authorization.controller;

import com.shop.authorization.dto.auth.AuthorizationForm;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.service.AuthorizationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/authorization")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<AccessRefreshTokens> authorize(@Valid @RequestBody AuthorizationForm form) {
        log.info("Trying to authorize user '{}'...", form.getLoginOrEmail());
        return ResponseEntity.ok().body(authorizationService.authorizeUser(form));
    }

}
