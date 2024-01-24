package com.shop.media.controller.advice;

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
import com.shop.media.dao.exception.MinIoFileGetException;
import com.shop.media.dao.exception.MinIoFileRemoveException;
import com.shop.media.dao.exception.MinIoFileUploadException;
import com.shop.media.dao.exception.MinIoStorageException;
import com.shop.media.service.exeption.FileReadingException;
import com.shop.media.service.exeption.FileUploadingException;
import com.shop.media.service.exeption.NotSupportedFileExtensionException;
import com.shop.media.service.exeption.file.extension.FileExtensionNotFoundException;
import com.shop.media.service.exeption.product.MediaElementNotBelongToProductException;
import com.shop.media.service.exeption.product.ProductMediaAlreadyExistsException;
import com.shop.media.service.exeption.product.ProductMediaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class MediaControllerAdvice {

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

    /** MinIO exceptions **/

    @ExceptionHandler({MinIoStorageException.class})
    protected ResponseEntity<AdviceResponseObject> minIoStorageException(Exception e, WebRequest request) {
        log.error("Resource storage exception! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Resource storage exception!", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({MinIoFileGetException.class})
    protected ResponseEntity<AdviceResponseObject> minIoFileGetException(Exception e, WebRequest request) {
        log.error("Exception while getting file from minio resource storage! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject(
                "Exception while getting file from minio resource storage!", e, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({MinIoFileUploadException.class, FileUploadingException.class})
    protected ResponseEntity<AdviceResponseObject> minIoFileUploadException(Exception e, WebRequest request) {
        log.error("Exception while saving file in minio resource storage! {}", e.getMessage());
        AdviceResponseObject response =
                new AdviceResponseObject("Exception while saving file in minio resource storage!", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({MinIoFileRemoveException.class})
    protected ResponseEntity<AdviceResponseObject> minIoFileRemoveException(Exception e, WebRequest request) {
        log.error("Exception while deleting file from minio resource storage! {}", e.getMessage());
        AdviceResponseObject response =
                new AdviceResponseObject("Exception while deleting file from minio resource storage!", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(FileReadingException.class)
    protected ResponseEntity<AdviceResponseObject> fileReadingException(Exception e, WebRequest request) {
        log.error("Exception while reading file! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Exception while reading file!", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(NotSupportedFileExtensionException.class)
    protected ResponseEntity<AdviceResponseObject> notSupportedFileExtensionException(Exception e, WebRequest request) {
        log.error("File extension is not supported! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("File extension is not supported!", e, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /** PRODUCT IMGS API EXCEPTIONS **/

    @ExceptionHandler(ProductMediaAlreadyExistsException.class)
    protected ResponseEntity<AdviceResponseObject> productMediaAlreadyExistsException(Exception e, WebRequest request) {
        log.error("Product media already exists! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Product media already exists!", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(MediaElementNotBelongToProductException.class)
    protected ResponseEntity<AdviceResponseObject> imageNotBelongToProduct(Exception e, WebRequest request) {
        log.error("Exception while loading image. Image is not belong to the product! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject(
                "Exception while loading image. Image is not belong to the product!",
                e,
                request
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ProductMediaNotFoundException.class)
    protected ResponseEntity<AdviceResponseObject> productMediaNotFoundException(Exception e, WebRequest request) {
        log.error("Exception while getting product! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Exception while getting product!", e, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /** File extension exceptions **/

    @ExceptionHandler(FileExtensionNotFoundException.class)
    protected ResponseEntity<AdviceResponseObject> fileExtensionNotFoundException(Exception e, WebRequest webRequest) {
        log.error("Exception while getting file extension! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Exception while getting file extension!", e, webRequest);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
