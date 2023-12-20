package com.shop.media.dao.exception;

public class MinIoFileGetException extends MinIoStorageException {

    public MinIoFileGetException() {
    }

    public MinIoFileGetException(String message) {
        super(message);
    }

    public MinIoFileGetException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinIoFileGetException(Throwable cause) {
        super(cause);
    }
}
