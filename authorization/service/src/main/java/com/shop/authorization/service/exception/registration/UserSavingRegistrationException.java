package com.shop.authorization.service.exception.registration;

import com.shop.common.utils.exception.dao.EntitySaveRepositoryException;

public class UserSavingRegistrationException extends EntitySaveRepositoryException {

    public UserSavingRegistrationException() {
    }

    public UserSavingRegistrationException(String message) {
        super(message);
    }

    public UserSavingRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserSavingRegistrationException(Throwable cause) {
        super(cause);
    }
}
