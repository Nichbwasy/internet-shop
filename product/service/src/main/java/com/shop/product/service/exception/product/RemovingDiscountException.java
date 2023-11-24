package com.shop.product.service.exception.product;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class RemovingDiscountException extends CommonServiceException {

    public RemovingDiscountException() {
    }

    public RemovingDiscountException(String message) {
        super(message);
    }

    public RemovingDiscountException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemovingDiscountException(Throwable cause) {
        super(cause);
    }
}
