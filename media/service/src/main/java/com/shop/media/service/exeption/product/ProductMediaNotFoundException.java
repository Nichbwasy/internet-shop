package com.shop.media.service.exeption.product;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class ProductMediaNotFoundException extends CommonServiceException {

    public ProductMediaNotFoundException() {
    }

    public ProductMediaNotFoundException(String message) {
        super(message);
    }

    public ProductMediaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductMediaNotFoundException(Throwable cause) {
        super(cause);
    }
}
