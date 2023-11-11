package com.shop.authorization.controller.exception.security;

import com.shop.common.utils.exception.controller.ControllerException;

public class SecurityException extends ControllerException {

    public SecurityException() {
    }

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityException(Throwable cause) {
        super(cause);
    }
}
