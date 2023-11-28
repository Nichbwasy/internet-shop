package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class NotEnoughProductsException extends CommonServiceException {

    public NotEnoughProductsException() {
    }

    public NotEnoughProductsException(String message) {
        super(message);
    }

    public NotEnoughProductsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughProductsException(Throwable cause) {
        super(cause);
    }
}
