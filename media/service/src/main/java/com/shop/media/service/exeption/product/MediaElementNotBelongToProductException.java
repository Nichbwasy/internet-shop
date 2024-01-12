package com.shop.media.service.exeption.product;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class MediaElementNotBelongToProductException extends CommonServiceException {

    public MediaElementNotBelongToProductException() {
    }

    public MediaElementNotBelongToProductException(String message) {
        super(message);
    }

    public MediaElementNotBelongToProductException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaElementNotBelongToProductException(Throwable cause) {
        super(cause);
    }
}
