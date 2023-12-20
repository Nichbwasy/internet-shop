package com.shop.media.dao.exception;

public class MinIoFileRemoveException extends MinIoStorageException {

    public MinIoFileRemoveException() {
    }

    public MinIoFileRemoveException(String message) {
        super(message);
    }

    public MinIoFileRemoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinIoFileRemoveException(Throwable cause) {
        super(cause);
    }
}
