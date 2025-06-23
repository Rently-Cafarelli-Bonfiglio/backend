package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Global exception handler for the Rently application.
 * This class handles and customizes the response for exceptions thrown by controllers.
 */
@RestControllerAdvice // Handles exceptions globally for all controllers
public class GlobalExceptionHandler {

    /**
     * Handles exceptions thrown when a coupon is already used.
     *
     * @param e the thrown CouponAlreadyUsedException
     * @return a ResponseEntity with the error message and HTTP 409 status
     */

    @ExceptionHandler(CouponAlreadyUsedException.class)
    public ResponseEntity<CustomResponse> handleCouponAlreadyUsedException(CouponAlreadyUsedException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles exceptions thrown when a coupon is expired.
     *
     * @param e the thrown CouponExpiredException
     * @return a ResponseEntity with the error message and HTTP 409 status
     */

    @ExceptionHandler(CouponExpiredException.class)
    public ResponseEntity<CustomResponse> handleCouponExpiredException(CouponExpiredException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles exceptions thrown during entity deletion.
     *
     * @param e the thrown EntityDeleteException
     * @return a ResponseEntity with the error message and HTTP 400 status
     */
    @ExceptionHandler(EntityDeleteException.class)
    public ResponseEntity<CustomResponse> handleEntityDeleteException(EntityDeleteException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions thrown during entity modification.
     *
     * @param e the thrown EntityModifyException
     * @return a ResponseEntity with the error message and HTTP 400 status
     */
    @ExceptionHandler(EntityModifyException.class)
    public ResponseEntity<CustomResponse> handleEntityModifyException(EntityModifyException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions when an entity is not found.
     *
     * @param e the thrown EntityNotFoundException
     * @return a ResponseEntity with the error message and HTTP 404 status
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomResponse> handleNotFoundException(EntityNotFoundException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions thrown during entity registration.
     *
     * @param e the thrown EntityRegistrationException
     * @return a ResponseEntity with the error message and HTTP 400 status
     */
    @ExceptionHandler(EntityRegistrationException.class)
    public ResponseEntity<CustomResponse> handleEntityRegistrationException(EntityRegistrationException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions thrown when a payment is rejected.
     *
     * @param e the thrown PaymentRejectedException
     * @return a ResponseEntity with the error message and HTTP 400 status
     */

    @ExceptionHandler(PaymentRejectedException.class)
    public ResponseEntity<CustomResponse> handlePaymentRejectedException(PaymentRejectedException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions thrown when a property is not available.
     *
     * @param e the thrown UnavailablePropertyException
     * @return a ResponseEntity with the error message and HTTP 400 status
     */

    @ExceptionHandler(UnavailablePropertyException.class)
    public ResponseEntity<CustomResponse> handleUnavailablePropertyException(UnavailablePropertyException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions thrown when a user is disabled.
     *
     * @param e the thrown UserDisabledException
     * @return a ResponseEntity with the error message and HTTP 403 status
     */

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<CustomResponse> handleUserDisabledException(UserDisabledException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles exceptions thrown when a user is enabled.
     *
     * @param e the thrown UserEnabledException
     * @return a ResponseEntity with the error message and HTTP 403 status
     */

    @ExceptionHandler(UserEnabledException.class)
    public ResponseEntity<CustomResponse> handleUserEnabledException(UserEnabledException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles exceptions thrown when a user is unauthorized.
     *
     * @param e the thrown UserUnauthorizedException
     * @return a ResponseEntity with the error message and HTTP 401 status
     */

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<CustomResponse> handleUserUnauthorizedException(UserUnauthorizedException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles data validation exceptions, including custom and constraint violations.
     *
     * @param e the thrown ConstraintViolationException
     * @return a ResponseEntity with a message and HTTP 400 status
     */
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<CustomResponse> handleDataValidationException(DataValidationException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles constraint violations that occur during data validation.
     *
     * @param e the thrown ConstraintViolationException
     * @return a ResponseEntity with a message and HTTP 400 status
     */

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomResponse> handleConstraintViolationException(ConstraintViolationException e) {
        CustomResponse response = new CustomResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles validation errors for method arguments annotated with @Valid.
     *
     * @param e the thrown MethodArgumentNotValidException
     * @return a ResponseEntity with validation messages and HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String validationMessages = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .reduce("", (acc, message) -> acc + message + ", ");
        return ResponseEntity.badRequest().body(new CustomResponse(validationMessages));
    }
}