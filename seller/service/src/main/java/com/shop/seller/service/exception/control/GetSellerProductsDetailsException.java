package com.shop.seller.service.exception.control;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class GetSellerProductsDetailsException extends CommonServiceException {

    public GetSellerProductsDetailsException() {
    }

    public GetSellerProductsDetailsException(String message) {
        super(message);
    }

    public GetSellerProductsDetailsException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetSellerProductsDetailsException(Throwable cause) {
        super(cause);
    }
}
