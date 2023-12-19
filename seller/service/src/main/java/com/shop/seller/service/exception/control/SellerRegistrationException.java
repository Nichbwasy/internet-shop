package com.shop.seller.service.exception.control;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class SellerRegistrationException extends CommonServiceException {
    public SellerRegistrationException() {
    }

    public SellerRegistrationException(String message) {
        super(message);
    }

    public SellerRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SellerRegistrationException(Throwable cause) {
        super(cause);
    }
}
