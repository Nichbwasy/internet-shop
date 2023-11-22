package com.shop.product.service.exception.product;

import com.shop.common.utils.all.exception.service.ServiceException;

public class AddingCategoryException extends ServiceException {

    public AddingCategoryException() {
    }

    public AddingCategoryException(String message) {
        super(message);
    }

    public AddingCategoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddingCategoryException(Throwable cause) {
        super(cause);
    }
}
