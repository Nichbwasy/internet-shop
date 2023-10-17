package com.shop.common.utils.exception.dao;

public class EntitySaveRepositoryException extends RepositoryException {

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
