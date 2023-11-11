package com.shop.authorization.service.exception.encoder;

import com.shop.common.utils.exception.service.ServiceException;

public class PasswordEncoderException extends ServiceException {

    public PasswordEncoderException() {
    }

    public PasswordEncoderException(String message) {
        super(message);
    }

    public PasswordEncoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordEncoderException(Throwable cause) {
        super(cause);
    }
}
