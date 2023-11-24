package com.shop.common.utils.all.exception.dao;

public class EntitySaveRepositoryException extends CommonRepositoryException {

    public EntitySaveRepositoryException() {
    }

    public EntitySaveRepositoryException(String message) {
        super(message);
    }

    public EntitySaveRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntitySaveRepositoryException(Throwable cause) {
        super(cause);
    }
}
