package com.shop.media.service.exeption;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class NotSupportedFileExtensionException extends CommonServiceException {

    public NotSupportedFileExtensionException() {
    }

    public NotSupportedFileExtensionException(String message) {
        super(message);
    }

    public NotSupportedFileExtensionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedFileExtensionException(Throwable cause) {
        super(cause);
    }
}
