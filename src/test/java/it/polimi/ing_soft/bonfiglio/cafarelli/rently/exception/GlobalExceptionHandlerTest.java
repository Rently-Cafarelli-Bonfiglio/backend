package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.swing.text.html.parser.Entity;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleCouponAlreadyUsedExceptionReturnsConflict() {
        CouponAlreadyUsedException ex = new CouponAlreadyUsedException();
        ResponseEntity<CustomResponse> response = exceptionHandler.handleCouponAlreadyUsedException(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Coupon already used", response.getBody().getMessage());
    }

    @Test
    void handleCouponExpiredExceptionReturnsConflict() {
        CouponExpiredException ex = new CouponExpiredException();
        ResponseEntity<CustomResponse> response = exceptionHandler.handleCouponExpiredException(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Coupon is expired", response.getBody().getMessage());
    }

    @Test
    void handleEntityDeleteExceptionReturnsBadRequest() {
        EntityDeleteException ex = new EntityDeleteException("Delete error");
        ResponseEntity<CustomResponse> response = exceptionHandler.handleEntityDeleteException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Delete error", response.getBody().getMessage());
    }

    @Test
    void handleEntityModifyExceptionReturnsBadRequest() {
        EntityModifyException ex = new EntityModifyException("Modify error");
        ResponseEntity<CustomResponse> response = exceptionHandler.handleEntityModifyException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Modify error", response.getBody().getMessage());
    }

    @Test
    void handleNotFoundExceptionReturnsNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException(Entity.class);
        ResponseEntity<CustomResponse> response = exceptionHandler.handleNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody().getMessage());
    }

    @Test
    void handleEntityRegistrationExceptionReturnsBadRequest() {
        EntityRegistrationException ex = new EntityRegistrationException("Registration error");
        ResponseEntity<CustomResponse> response = exceptionHandler.handleEntityRegistrationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration error", response.getBody().getMessage());
    }

    @Test
    void handlePaymentRejectedExceptionReturnsBadRequest() {
        PaymentRejectedException ex = new PaymentRejectedException("Payment rejected");
        ResponseEntity<CustomResponse> response = exceptionHandler.handlePaymentRejectedException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Payment rejected", response.getBody().getMessage());
    }

    @Test
    void handleUnavailablePropertyExceptionReturnsBadRequest() {
        UnavailablePropertyException ex = new UnavailablePropertyException("Property unavailable");
        ResponseEntity<CustomResponse> response = exceptionHandler.handleUnavailablePropertyException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Property unavailable", response.getBody().getMessage());
    }

    @Test
    void handleUserDisabledExceptionReturnsForbidden() {
        UserDisabledException ex = new UserDisabledException("User disabled");
        ResponseEntity<CustomResponse> response = exceptionHandler.handleUserDisabledException(ex);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User disabled", response.getBody().getMessage());
    }

    @Test
    void handleUserEnabledExceptionReturnsForbidden() {
        UserEnabledException ex = new UserEnabledException("User already enabled");
        ResponseEntity<CustomResponse> response = exceptionHandler.handleUserEnabledException(ex);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User already enabled", response.getBody().getMessage());
    }

    @Test
    void handleUserUnauthorizedExceptionReturnsUnauthorized() {
        UserUnauthorizedException ex = new UserUnauthorizedException("Unauthorized");
        ResponseEntity<CustomResponse> response = exceptionHandler.handleUserUnauthorizedException(ex);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody().getMessage());
    }

    @Test
    void handleDataValidationExceptionReturnsBadRequest() {
        DataValidationException ex = new DataValidationException("Invalid data");
        ResponseEntity<CustomResponse> response = exceptionHandler.handleDataValidationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data", response.getBody().getMessage());
    }

    @Test
    void handleConstraintViolationExceptionReturnsBadRequest() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        when(ex.getMessage()).thenReturn("Constraint violated");
        ResponseEntity<CustomResponse> response = exceptionHandler.handleConstraintViolationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Constraint violated", response.getBody().getMessage());
    }

    @Test
    void handleMethodArgumentNotValidExceptionReturnsBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("objectName", "field1", "message1");
        FieldError error2 = new FieldError("objectName", "field2", "message2");

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        ResponseEntity<CustomResponse> response = exceptionHandler.handleMethodArgumentNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("message1"));
        assertTrue(response.getBody().getMessage().contains("message2"));
    }
}