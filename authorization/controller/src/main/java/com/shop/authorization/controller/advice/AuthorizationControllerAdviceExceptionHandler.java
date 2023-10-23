package com.shop.authorization.controller.advice;

import com.shop.authorization.service.exception.authorization.LoginEmailValidationAuthorizationException;
import com.shop.authorization.service.exception.authorization.PasswordNotMatchAuthorizationException;
import com.shop.authorization.service.exception.authorization.UserNotFoundAuthorizationException;
import com.shop.authorization.service.exception.encoder.PasswordEncoderException;
import com.shop.authorization.service.exception.registration.EmailAlreadyExistsRegistrationException;
import com.shop.authorization.service.exception.registration.LoginAlreadyExistsRegistrationException;
import com.shop.authorization.service.exception.registration.PasswordsNotMatchRegistrationException;
import com.shop.authorization.service.exception.registration.UserSavingRegistrationException;
import com.shop.common.utils.exception.dao.*;
import com.shop.common.utils.exception.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class AuthorizationControllerAdviceExceptionHandler extends ResponseEntityExceptionHandler {

    /** Common exceptions **/

    @ExceptionHandler({
            Exception.class,
            RuntimeException.class})
    protected ResponseEntity<AdviceResponseObject> otherExceptions(Exception e, WebRequest request) {
        log.error("Internal server error! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Internal server error!", e, request);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler({ServiceException.class})
    protected ResponseEntity<AdviceResponseObject> serviceException(Exception e, WebRequest request) {
        log.error("Service exception! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Service exception!", e, request);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler({RepositoryException.class})
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
        log.error("Unable save record! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Unable save record!", e, request);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler({EntityNotFoundRepositoryException.class})
    protected ResponseEntity<AdviceResponseObject> noRecord(Exception e, WebRequest request) {
        log.error("Record not found! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Record not found!", e, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /** Registration exceptions w**/

    @ExceptionHandler({
            LoginAlreadyExistsRegistrationException.class,
            EmailAlreadyExistsRegistrationException.class})
    protected ResponseEntity<AdviceResponseObject> userAlreadyExists(Exception e, WebRequest request) {
        log.error("User with the same login or email already exists! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject(
                "User with the same login or email already exists!", e, request
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({PasswordsNotMatchRegistrationException.class})
    protected ResponseEntity<AdviceResponseObject> notMatchedPasswords(Exception e, WebRequest request) {
        log.error("Passwords doesn't match! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Passwords doesn't match!", e, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({UserSavingRegistrationException.class})
    protected ResponseEntity<AdviceResponseObject> registerUserException(Exception e, WebRequest request) {
        log.error("Unable register user! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Unable register user!", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /** Authorization exceptions **/

    @ExceptionHandler({LoginEmailValidationAuthorizationException.class})
    protected ResponseEntity<AdviceResponseObject> loginEmailInvalid(Exception e, WebRequest request) {
        log.error("Login or email has wrong format! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Login or email has wrong format!", e, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({PasswordNotMatchAuthorizationException.class})
    protected ResponseEntity<AdviceResponseObject> wrongPassword(Exception e, WebRequest request) {
        log.error("Password doesn't match! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Password doesn't match!", e, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({UserNotFoundAuthorizationException.class})
    protected ResponseEntity<AdviceResponseObject> userNotFound(Exception e, WebRequest request) {
        log.error("Unable find a user with such login or email! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject(
                "Unable find a user with such login or email!", e, request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /** Encoder exceptions **/

    @ExceptionHandler({PasswordEncoderException.class})
    protected ResponseEntity<AdviceResponseObject> encoderException(Exception e, WebRequest request) {
        log.error("Password encoder exception! {}", e.getMessage());
        AdviceResponseObject response = new AdviceResponseObject("Password encoder exception!", e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // TODO: Add handling for jwt tokens filter
}
