package com.shop.product.service.exception.category;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class AddingSubCategoryException extends CommonServiceException {

    public AddingSubCategoryException() {
    }

    public AddingSubCategoryException(String message) {
        super(message);
    }

    public AddingSubCategoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddingSubCategoryException(Throwable cause) {
        super(cause);
    }
}
