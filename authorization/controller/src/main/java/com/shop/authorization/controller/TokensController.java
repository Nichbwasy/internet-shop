package com.shop.authorization.controller;

import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.service.TokensService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/tokens")
public class TokensController {

    private final TokensService tokensService;

    public TokensController(TokensService tokensService) {
        this.tokensService = tokensService;
    }

    @PostMapping
    public ResponseEntity<AccessRefreshTokens> updateUserToken(@RequestBody String refreshToken) {
        log.info("Trying to update access/refresh tokens for the user with id ...");
        return ResponseEntity.ok().body(tokensService.refreshTokens(refreshToken));
    }

}
