package com.shop.seller.service.exception.control;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class UpdateSellerProductException extends CommonServiceException {

    public UpdateSellerProductException() {
    }

    public UpdateSellerProductException(String message) {
        super(message);
    }

    public UpdateSellerProductException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateSellerProductException(Throwable cause) {
        super(cause);
    }
}
