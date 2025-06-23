package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.LoginResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserLoginRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserRegistrationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.AuthService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller for handling authentication-related operations in the application.
 * <p>
 * This controller provides endpoints for user registration and login functionality.
 * It processes authentication requests and returns appropriate responses with tokens
 * for authenticated users or registration confirmations for new users.
 * </p>
 */
@RestController
@RequestMapping(ApiPathUtil.REST_PATH + "/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Authentication API for user registration and login")
public class AuthController {
    /**
     * Service responsible for handling user authentication and registration operations.
     */
    private final AuthService authenticationService;

    private final Validator validator;

    /**
     * Registers a new user in the system.
     * <p>
     * This endpoint accepts user registration details, validates them,
     * creates a new user account, and returns a confirmation response.
     * </p>
     *
     * @param userRegistrationRequestJson DTO containing the user registration information
     * @return ResponseEntity with the registration result message and HTTP status 201 (Created)
     */
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided registration details and profile image"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @Content
            )
    })
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomResponse> register(
            @RequestPart(value = "userRegistrationRequest") String userRegistrationRequestJson,
            @RequestPart(required = false) MultipartFile image) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        UserRegistrationRequest userRegistrationRequest = objectMapper.readValue(
                userRegistrationRequestJson,
                UserRegistrationRequest.class);

        var violations = validator.validate(userRegistrationRequest);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        final CustomResponse response = authenticationService.registerUser(userRegistrationRequest, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Authenticates a user and provides a JWT token on successful login.
     * <p>
     * This endpoint verifies user credentials and, if valid, generates
     * and returns a JWT token that can be used for subsequent authenticated requests.
     * </p>
     *
     * @param userLoginRequest DTO containing the user login credentials
     * @return ResponseEntity with login result including JWT token on successful authentication
     */
    @Operation(
            summary = "Authenticate user",
            description = "Verifies user credentials and returns a JWT token for authenticated access"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(
            @Parameter(description = "User login credentials", required = true)
            @Valid @RequestBody UserLoginRequest userLoginRequest) {
        final LoginResponse response = authenticationService.authenticateUser(userLoginRequest);

        return ResponseEntity
                .ok()
                .body(response);
    }
}