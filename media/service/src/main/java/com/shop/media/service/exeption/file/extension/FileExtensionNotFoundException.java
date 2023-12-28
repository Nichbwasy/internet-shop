package com.shop.media.service.exeption.file.extension;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class FileExtensionNotFoundException extends CommonServiceException {

    public FileExtensionNotFoundException() {
    }

    public FileExtensionNotFoundException(String message) {
        super(message);
    }

    public FileExtensionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileExtensionNotFoundException(Throwable cause) {
        super(cause);
    }
}
