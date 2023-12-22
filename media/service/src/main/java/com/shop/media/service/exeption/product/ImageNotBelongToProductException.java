package com.shop.media.service.exeption.product;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class ImageNotBelongToProductException extends CommonServiceException {

    public ImageNotBelongToProductException() {
    }

    public ImageNotBelongToProductException(String message) {
        super(message);
    }

    public ImageNotBelongToProductException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageNotBelongToProductException(Throwable cause) {
        super(cause);
    }
}
