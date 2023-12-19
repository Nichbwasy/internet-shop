package com.shop.seller.service.exception.control;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class AddNewProductException extends CommonServiceException {

    public AddNewProductException() {
    }

    public AddNewProductException(String message) {
        super(message);
    }

    public AddNewProductException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddNewProductException(Throwable cause) {
        super(cause);
    }
}
