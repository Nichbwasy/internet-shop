package com.shop.seller.service.exception.control;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class ProductNotBelongToSellerException extends CommonServiceException {

    public ProductNotBelongToSellerException() {
    }

    public ProductNotBelongToSellerException(String message) {
        super(message);
    }

    public ProductNotBelongToSellerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductNotBelongToSellerException(Throwable cause) {
        super(cause);
    }
}
