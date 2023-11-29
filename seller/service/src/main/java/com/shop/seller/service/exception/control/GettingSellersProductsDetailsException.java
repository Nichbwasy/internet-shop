package com.shop.seller.service.exception.control;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class GettingSellersProductsDetailsException extends CommonServiceException {

    public GettingSellersProductsDetailsException() {
    }

    public GettingSellersProductsDetailsException(String message) {
        super(message);
    }

    public GettingSellersProductsDetailsException(String message, Throwable cause) {
        super(message, cause);
    }

    public GettingSellersProductsDetailsException(Throwable cause) {
        super(cause);
    }
}
