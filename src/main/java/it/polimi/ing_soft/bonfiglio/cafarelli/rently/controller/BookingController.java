package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.BookingCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.BookingDashboardResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UnavailablePropertyException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UserUnauthorizedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.BookingService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling booking-related requests.
 * This class provides endpoints for managing bookings.
 */
@RestController
@RequestMapping(ApiPathUtil.REST_PATH + "/booking")
@AllArgsConstructor
@Tag(name = "Bookings", description = "API for booking management operations")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Saves a new booking.
     * <p>
     * This endpoint allows clients to create a new booking for a property.
     * </p>
     *
     * @param request the request containing booking details
     * @return a response entity with a message indicating the result of the operation
     * @throws IllegalArgumentException if the booking details are invalid
     * @throws EntityNotFoundException if the property or user is not found
     * @throws UnavailablePropertyException if the property is not available for the requested dates
     * @throws UserUnauthorizedException if the user is not authorized to make the booking
     */
    @Operation(
        summary = "Create a new booking",
        description = "Creates a new booking for a property with the specified details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking created successfully",
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
            responseCode = "404",
            description = "Property or user not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Property unavailable for the requested dates",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "User unauthorized to make this booking",
            content = @Content
        )
    })
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<CustomResponse> saveBooking(
        @Parameter(description = "Booking details including property ID, user ID, and dates", required = true)
        @Valid @RequestBody BookingCreateRequest request)
            throws IllegalArgumentException, EntityNotFoundException, UnavailablePropertyException, UserUnauthorizedException {

        CustomResponse response = bookingService.saveBooking(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all bookings (Admin)",
        description = "Retrieves a list of all bookings in the system - Admin access only"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of bookings retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Booking.class))
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required",
            content = @Content
        )
    })
    @GetMapping("/bookings")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves all bookings made by a specific user.
     * <p>
     * This endpoint returns a list of all bookings that were made by the specified user.
     * </p>
     *
     * @param userId the ID of the user whose bookings to retrieve
     * @return a response entity with a list of the user's bookings
     */
    @Operation(
        summary = "Get user's bookings",
        description = "Retrieves all bookings made by a specific user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of user's bookings retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Booking.class))
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Unauthorized to view these bookings",
            content = @Content
        )
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<List<Booking>> getAllBookings(
        @Parameter(description = "ID of the user whose bookings to retrieve", required = true)
        @PathVariable Long userId) {
        List<Booking> bookings = bookingService.getAllBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(
        summary = "Cancel booking",
        description = "Cancels an existing booking"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking successfully cancelled",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Unauthorized to cancel this booking",
            content = @Content
        )
    })
    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN', 'ROLE_MODERATOR')")
    public ResponseEntity<CustomResponse> cancelBooking(
            @Parameter(description = "ID of the booking to cancel", required = true)
            @PathVariable Long bookingId) {
        CustomResponse response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/host/{hostId}")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<List<BookingDashboardResponse>> getAllBookingsByHostId(
            @Parameter(description = "ID of the host whose bookings to retrieve", required = true)
            @PathVariable Long hostId) {
        List<BookingDashboardResponse> bookings = bookingService.getAllBookingsByHostId(hostId);
        return ResponseEntity.ok(bookings);
    }
}
