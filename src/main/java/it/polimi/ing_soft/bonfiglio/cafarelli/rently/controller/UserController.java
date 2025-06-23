package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserModifyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserPasswordChangeRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for managing user-related operations.
 * <p>
 * This controller provides endpoints for user profile management, including
 * profile modifications, password changes, account status management, and
 * favorite properties handling.
 * </p>
 */
@RestController
@RequestMapping(ApiPathUtil.REST_PATH + "/user")
@AllArgsConstructor
@Tag(name = "Users", description = "API for user management and profile operations")
public class UserController {
    /**
     * Service responsible for handling business logic related to user operations.
     */
    private final UserService userService;

    private final Validator validator;

    /**
     * Modifies a user's profile information.
     * <p>
     * This endpoint allows users to update their profile details such as name,
     * contact information, and other personal data.
     * </p>
     *
     * @param userModifyRequestJson DTO containing the user information to update
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
            summary = "Modify user profile",
            description = "Updates a user's profile information and optionally their profile image"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile successfully updated",
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
                    responseCode = "401",
                    description = "Unauthorized - User not authenticated",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @PostMapping(value="/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_HOST', 'ROLE_CLIENT')")
    public ResponseEntity<CustomResponse> modify(
            @Parameter(description = "User profile information to update", required = true)
            @RequestPart (value = "UserModifyRequest") String userModifyRequestJson,
            @Parameter(description = "New profile image (optional)")
            @RequestPart(required = false) MultipartFile image) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        UserModifyRequest userModifyRequest = objectMapper.readValue(
                userModifyRequestJson,
                UserModifyRequest.class);

        var violations = validator.validate(userModifyRequest);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        CustomResponse response;

        if (image != null && !image.isEmpty()) {
            response = userService.modifyWithImage(userModifyRequest, image);
        } else {
            response = userService.modify(userModifyRequest);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Changes a user's password.
     * <p>
     * This endpoint allows users to update their account password after
     * providing both the current and new password.
     * </p>
     *
     * @param userPasswordChangeRequest DTO containing the current and new password
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
            summary = "Change user password",
            description = "Updates a user's password after verifying the current password"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password successfully changed",
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
                    responseCode = "401",
                    description = "Unauthorized - Current password is incorrect",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @PostMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('ROLE_HOST', 'ROLE_CLIENT')")
    public ResponseEntity<CustomResponse> changePassword(
            @Parameter(description = "Password change request with current and new password", required = true)
            @Valid @RequestBody UserPasswordChangeRequest userPasswordChangeRequest) {
        CustomResponse response = userService.changePassword(userPasswordChangeRequest);

        // Check if the response message indicates success or failure
        return ResponseEntity
                .ok(response);
    }

    /**
     * Disables a user account.
     * <p>
     * This endpoint allows administrators to disable a user account,
     * preventing the user from accessing the system.
     * </p>
     *
     * @param userId the ID of the user to disable
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
            summary = "Disable user account",
            description = "Disables a user account, preventing the user from accessing the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User account successfully disabled",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Not an administrator",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @PatchMapping("/disable/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public ResponseEntity<CustomResponse> disable(
            @Parameter(description = "ID of the user to disable", required = true)
            @PathVariable Long userId) {
        CustomResponse response = userService.disable(userId);
        return ResponseEntity
                .ok(response);
    }

    /**
     * Enables a previously disabled user account.
     * <p>
     * This endpoint allows administrators to re-enable a previously disabled
     * user account, restoring the user's access to the system.
     * </p>
     *
     * @param userId the ID of the user to enable
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
            summary = "Enable user account",
            description = "Re-enables a previously disabled user account, restoring access to the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User account successfully enabled",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Not an administrator",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @PatchMapping("/enable/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public ResponseEntity<CustomResponse> enable(
            @Parameter(description = "ID of the user to enable", required = true)
            @PathVariable Long userId) {
        CustomResponse response = userService.enable(userId);

        return ResponseEntity
                .ok(response);
    }

    /**
     * Retrieves the currently authenticated user's profile.
     * <p>
     * This endpoint returns the full profile information of the currently
     * logged-in user based on their authentication token.
     * </p>
     *
     * @return ResponseEntity containing the current user's profile
     */
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - User not authenticated",
                    content = @Content
            )
    })
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ROLE_HOST', 'ROLE_CLIENT')")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    /**
     * Retrieves all users in the system.
     * <p>
     * This endpoint returns a list of all registered users in the application.
     * Typically used by administrators for user management.
     * </p>
     *
     * @return ResponseEntity containing a list of all users
     */
    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all registered users in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of users retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Not an administrator",
                    content = @Content
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN' , 'ROLE_MODERATOR')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Retrieves all favorite properties for a specific user.
     * <p>
     * This endpoint returns a list of properties that have been marked as
     * favorites by the specified user.
     * </p>
     *
     * @param username the username of the user whose favorites to retrieve
     * @return ResponseEntity containing a list of favorite properties
     */
    @Operation(
            summary = "Get user's favorite properties",
            description = "Retrieves all properties marked as favorites by a specific user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of favorite properties retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Property.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @GetMapping("/favorite-properties")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<List<Property>> getAllFavoriteProperties(
            @Parameter(description = "Username of the user whose favorites to retrieve", required = true)
            @RequestParam String username) {
        return ResponseEntity.ok(userService.getAllFavoriteProperties(username));
    }

    /**
     * Adds a property to a user's favorites list.
     * <p>
     * This endpoint allows a user to mark a property as favorite for
     * easier access in the future.
     * </p>
     *
     * @param username the username of the user adding the favorite
     * @param propertyId the ID of the property to add to favorites
     * @return ResponseEntity containing the updated user object
     */
    @Operation(
            summary = "Add property to favorites",
            description = "Adds a property to a user's list of favorite properties"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Property successfully added to favorites",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or property not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Property already in favorites",
                    content = @Content
            )
    })
    @PostMapping("/favorite-properties/add")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<User> addFavoriteProperty(
            @Parameter(description = "Username of the user adding the favorite", required = true)
            @RequestParam String username,

            @Parameter(description = "ID of the property to add to favorites", required = true)
            @RequestParam Long propertyId) {
        return ResponseEntity.ok(userService.addFavoriteProperty(username, propertyId));
    }

    /**
     * Removes a property from a user's favorites list.
     * <p>
     * This endpoint allows a user to remove a property from their favorites list.
     * </p>
     *
     * @param username the username of the user removing the favorite
     * @param propertyId the ID of the property to remove from favorites
     * @return ResponseEntity containing the updated user object
     */
    @Operation(
            summary = "Remove property from favorites",
            description = "Removes a property from a user's list of favorite properties"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Property successfully removed from favorites",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or property not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Property not in favorites",
                    content = @Content
            )
    })
    @PostMapping("/favorite-properties/remove")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<User> removeFavoriteProperty(
            @Parameter(description = "Username of the user removing the favorite", required = true)
            @RequestParam String username,

            @Parameter(description = "ID of the property to remove from favorites", required = true)
            @RequestParam Long propertyId) {
        return ResponseEntity.ok(userService.removeFavoriteProperty(username, propertyId));
    }
    @PostMapping("/recharge-balance")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<User> rechargeBalance(
            @Parameter(description = "Username dell'utente da ricaricare", required = true)
            @RequestParam String username,

            @Parameter(description = "Importo da aggiungere al saldo", required = true)
            @RequestParam BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("L'importo deve essere maggiore di zero");
        }

        return ResponseEntity.ok(userService.rechargeBalance(username, amount));
    }
}