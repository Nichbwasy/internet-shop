package com.shop.authorization.service.exception.registration;

public class RefreshTokenSavingRegistrationException extends UserSavingRegistrationException {

    public RefreshTokenSavingRegistrationException() {
    }

    public RefreshTokenSavingRegistrationException(String message) {
        super(message);
    }

    public RefreshTokenSavingRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenSavingRegistrationException(Throwable cause) {
        super(cause);
    }
}
