package com.shop.common.utils.all.exception.controller;

public class CommonControllerException extends RuntimeException {

    public CommonControllerException() {
    }

    public CommonControllerException(String message) {
        super(message);
    }

    public CommonControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonControllerException(Throwable cause) {
        super(cause);
    }
}
