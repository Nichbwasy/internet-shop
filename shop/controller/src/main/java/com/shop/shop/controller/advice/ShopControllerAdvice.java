package com.shop.shop.controller.advice;

import com.shop.common.utils.all.dto.advice.AdviceResponseObject;
import com.shop.common.utils.all.exception.dao.*;
import com.shop.common.utils.all.exception.service.CommonServiceException;
import com.shop.common.utils.exception.jwt.JwtFilterSecurityException;
import com.shop.common.utils.exception.jwt.JwtTokenNotFoundException;
import com.shop.common.utils.exception.jwt.JwtTokenValidationException;
import com.shop.common.utils.exception.jwt.token.JwtTokenExpiredException;
import com.shop.common.utils.exception.jwt.token.JwtTokenMalformedException;
import com.shop.common.utils.exception.jwt.token.JwtTokenUnsupportedException;
import com.shop.common.utils.exception.jwt.token.JwtTokenWrongSignatureException;
import com.shop.shop.service.exception.shop.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class ShopControllerAdvice {

    /** Common exceptions **/

    @ExceptionHandler({
            Exception.class,
            RuntimeException.class})
    protected ResponseEntity<AdviceResponseObject> otherExceptions(Exception e, WebRequest request) {
        log.error("Internal server error! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Internal server error!", e, request);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler({CommonServiceException.class})
    protected ResponseEntity<AdviceResponseObject> serviceException(Exception e, WebRequest request) {
        log.error("Service exception! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Service exception!", e, request);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler({CommonRepositoryException.class})
    protected ResponseEntity<AdviceResponseObject> repositoryException(Exception e, WebRequest request) {
        log.error("Repository exception! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Repository exception!", e, request);
        return ResponseEntity.internalServerError().body(response);
    }

    /** Common repository exceptions **/

    @ExceptionHandler({EntityDeleteRepositoryException.class})
    protected ResponseEntity<AdviceResponseObject> badRequest(Exception e, WebRequest request) {
        log.error("Unable delete record! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Unable delete record!", e, request);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler({EntityUpdateRepositoryException.class})
    protected ResponseEntity<AdviceResponseObject> updateException(Exception e, WebRequest request) {
        log.error("Unable update record! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Unable update record!", e, request);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler({EntitySaveRepositoryException.class})
    protected ResponseEntity<AdviceResponseObject> savingException(Exception e, WebRequest request) {
        log.error("Unable save a record! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Unable save a record!", e, request);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler({EntityGetRepositoryException.class})
    protected ResponseEntity<AdviceResponseObject> getException(Exception e, WebRequest request) {
        log.error("Unable get a record! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Unable get a record!", e, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({EntityNotFoundRepositoryException.class})
    protected ResponseEntity<AdviceResponseObject> noRecord(Exception e, WebRequest request) {
        log.error("Record not found! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Record not found!", e, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /** Security exceptions **/

    @ExceptionHandler({SecurityException.class})
    protected ResponseEntity<AdviceResponseObject> securityException(Exception e, WebRequest request) {
        log.error("Security exception! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Security exception!", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({JwtFilterSecurityException.class})
    protected ResponseEntity<AdviceResponseObject> jwtFilterException(Exception e, WebRequest request) {
        log.error("Exception while filtering jwt token in request! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject(
                "Exception while filtering jwt token in request! {}",
                e,
                request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({JwtTokenNotFoundException.class})
    protected ResponseEntity<AdviceResponseObject> jwtTokenNotFoundException(Exception e, WebRequest request) {
        log.error("Token not found! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Token not found!", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({
            JwtTokenValidationException.class,
            JwtTokenExpiredException.class,
            JwtTokenMalformedException.class,
            JwtTokenUnsupportedException.class,
            JwtTokenWrongSignatureException.class
    })
    protected ResponseEntity<AdviceResponseObject> jwtValidationException(Exception e, WebRequest request) {
        log.error("Jwt token validation exception! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Jwt token validation exception", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({NullProductClientException.class, GetProductsClientException.class})
    protected ResponseEntity<AdviceResponseObject> getProductsClientException(Exception e, WebRequest request) {
        log.error("Unable to get product(s) from products microservice! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject(
                "Unable to get product(s) from products microservice!", e, request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    protected ResponseEntity<AdviceResponseObject> productsNotFoundException(Exception e, WebRequest request) {
        log.error("Nothing was found for product! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject(
                "Nothing was found for product!", e, request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({ProductsNotFoundForFilterException.class})
    protected ResponseEntity<AdviceResponseObject> productsNotFoundForFilterException(Exception e, WebRequest request) {
        log.error("Nothing was found for such filtering request! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject(
                "Nothing was found for such filtering request!", e, request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({ProductNotApprovedException.class})
    protected ResponseEntity<AdviceResponseObject> productNotApprovedException(Exception e, WebRequest request) {
        log.error("Can't add a product to cart! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Can't add a product to cart!", e, request);
        return ResponseEntity.badRequest().body(response);
    }

}
