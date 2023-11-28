package com.shop.seller.service.exception.control;

import com.shop.common.utils.all.exception.client.CommonMicroserviceClientException;

public class GetSellerDataUserApiClientException extends CommonMicroserviceClientException {

    public GetSellerDataUserApiClientException() {
    }

    public GetSellerDataUserApiClientException(String message) {
        super(message);
    }

    public GetSellerDataUserApiClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetSellerDataUserApiClientException(Throwable cause) {
        super(cause);
    }
}
