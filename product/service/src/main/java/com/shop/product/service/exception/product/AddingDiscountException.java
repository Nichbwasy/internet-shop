package com.shop.product.service.exception.product;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class AddingDiscountException extends CommonServiceException {

    public AddingDiscountException() {
    }

    public AddingDiscountException(String message) {
        super(message);
    }

    public AddingDiscountException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddingDiscountException(Throwable cause) {
        super(cause);
    }
}
