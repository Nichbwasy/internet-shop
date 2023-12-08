package com.shop.seller.service.exception.control;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class RemoveProductFromSellerException extends CommonServiceException {

    public RemoveProductFromSellerException() {
    }

    public RemoveProductFromSellerException(String message) {
        super(message);
    }

    public RemoveProductFromSellerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoveProductFromSellerException(Throwable cause) {
        super(cause);
    }
}
