package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.client.CommonMicroserviceClientException;

public class ChangeProductDataException extends CommonMicroserviceClientException {

    public ChangeProductDataException() {
    }

    public ChangeProductDataException(String message) {
        super(message);
    }

    public ChangeProductDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChangeProductDataException(Throwable cause) {
        super(cause);
    }
}
