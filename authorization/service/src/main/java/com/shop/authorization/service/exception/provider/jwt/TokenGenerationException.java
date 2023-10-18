package com.shop.authorization.service.exception.provider.jwt;

import com.shop.common.utils.exception.service.ServiceException;

public class TokenGenerationException extends ServiceException {

    public TokenGenerationException() {
    }

    public TokenGenerationException(String message) {
        super(message);
    }

    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenGenerationException(Throwable cause) {
        super(cause);
    }
}
