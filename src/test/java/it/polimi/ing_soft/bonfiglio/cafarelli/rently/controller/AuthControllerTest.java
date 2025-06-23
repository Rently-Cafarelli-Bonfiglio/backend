package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserLoginRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserRegistrationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.LoginResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.AuthService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private Validator validator;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_ValidRequest_ShouldReturnCreated() throws IOException {
        UserRegistrationRequest request = new UserRegistrationRequest();
        String json = objectMapper.writeValueAsString(request);
        MockMultipartFile image = new MockMultipartFile("image", "test.png", "image/png", new byte[]{});

        when(validator.validate(any(UserRegistrationRequest.class))).thenReturn(Collections.emptySet());
        when(authService.registerUser(any(), any())).thenReturn(new CustomResponse("User registered"));

        ResponseEntity<CustomResponse> response = authController.register(json, image);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // Usa il getter per accedere al messaggio
        assertEquals("User registered", response.getBody().getMessage());
    }

    @Test
    void register_InvalidRequest_ShouldThrowConstraintViolationException() throws IOException {
        UserRegistrationRequest request = new UserRegistrationRequest();
        String json = objectMapper.writeValueAsString(request);

        // Crea un set di violazioni generico
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        violations.add(violation);

        // Usa il tipo corretto per il mock
        when(validator.validate(any())).thenReturn((Set) violations);

        assertThrows(ConstraintViolationException.class, () -> authController.register(json, null));
    }

    @Test
    void authenticate_ValidCredentials_ShouldReturnOk() {
        // Crea l'oggetto request secondo il costruttore corretto
        UserLoginRequest loginRequest = new UserLoginRequest();
        // Imposta i campi necessari
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        // Crea la risposta con i parametri corretti per il costruttore
        LoginResponse expectedResponse = new LoginResponse("jwt.token");

        when(authService.authenticateUser(loginRequest)).thenReturn(expectedResponse);

        ResponseEntity<LoginResponse> response = authController.authenticate(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}