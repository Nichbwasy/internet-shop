package com.shop.common.utils.all.exception.security;


import com.shop.common.utils.all.exception.controller.CommonControllerException;

public class CommonSecurityException extends CommonControllerException {

    public CommonSecurityException() {
    }

    public CommonSecurityException(String message) {
        super(message);
    }

    public CommonSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonSecurityException(Throwable cause) {
        super(cause);
    }
}
