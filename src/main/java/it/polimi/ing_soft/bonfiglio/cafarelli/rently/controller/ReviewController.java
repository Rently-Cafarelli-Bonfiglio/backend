package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.HostResponseRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ReviewCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ReviewUpdateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Review;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.ReviewService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing review-related operations.
 * <p>
 * This controller provides endpoints for creating, updating, deleting, and retrieving
 * reviews for properties. It ensures that users can only review properties they have
 * booked and stayed at.
 * </p>
 */
@RestController
@RequestMapping(ApiPathUtil.REST_PATH + "/review")
@AllArgsConstructor
@Tag(name = "Reviews", description = "API for review management operations")
public class ReviewController {
    /**
     * Service responsible for handling business logic related to reviews.
     */
    private final ReviewService reviewService;

    /**
     * Creates a new review for a property.
     * <p>
     * This endpoint allows users to submit reviews for properties they have stayed at.
     * </p>
     *
     * @param propertyId the ID of the property being reviewed
     * @param reviewCreateRequest DTO containing review details
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Create a new review",
        description = "Submits a review for a property the user has stayed at"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Review successfully submitted",
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
            responseCode = "403",
            description = "Forbidden - User has not stayed at this property",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Property not found",
            content = @Content
        )
    })
    @PostMapping("/property/{propertyId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<CustomResponse> createPropertyReview(
        @Parameter(description = "ID of the property to review", required = true)
        @PathVariable Long propertyId,

        @Parameter(description = "Review details", required = true)
        @Valid @RequestBody ReviewCreateRequest reviewCreateRequest) {

        CustomResponse response = reviewService.createPropertyReview(propertyId, reviewCreateRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new review for a user.
     * <p>
     * This endpoint allows hosts to submit reviews for users who have stayed at their properties.
     * </p>
     *
     * @param reviewedUserId the ID of the user being reviewed
     * @param reviewCreateRequest DTO containing review details
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Create a new user review",
        description = "Submits a review for a user who has stayed at the host's property"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Review successfully submitted",
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
            responseCode = "403",
            description = "Forbidden - User is not a HOST or has not hosted this user",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<CustomResponse> createUserReview(
            @Parameter(description = "ID of the user to review", required = true)
            @PathVariable Long reviewedUserId,

            @Parameter(description = "Review details", required = true)
            @Valid @RequestBody ReviewCreateRequest reviewCreateRequest) {

        CustomResponse response = reviewService.createUserReview(reviewedUserId, reviewCreateRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing review.
     * <p>
     * This endpoint allows users to modify their own reviews.
     * </p>
     *
     * @param reviewId the ID of the review to update
     * @param reviewUpdateRequest DTO containing updated review details
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Update an existing review",
        description = "Modifies a review previously submitted by the user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Review successfully updated",
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
            responseCode = "403",
            description = "Forbidden - User is not the author of the review",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Review not found",
            content = @Content
        )
    })
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_HOST')")
    public ResponseEntity<CustomResponse> updateReview(
        @Parameter(description = "ID of the review to update", required = true)
        @PathVariable Long reviewId,

        @Parameter(description = "Updated review details", required = true)
        @Valid @RequestBody ReviewUpdateRequest reviewUpdateRequest) {

        CustomResponse response = reviewService.updateReview(reviewId, reviewUpdateRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a review.
     * <p>
     * This endpoint allows users to delete their own reviews or property owners to delete reviews for their properties.
     * </p>
     *
     * @param reviewId the ID of the review to delete
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Delete a review",
        description = "Removes a review from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Review successfully deleted",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User is not authorized to delete this review",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Review not found",
            content = @Content
        )
    })
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_HOST')")
    public ResponseEntity<CustomResponse> deleteReview(
        @Parameter(description = "ID of the review to delete", required = true)
        @PathVariable Long reviewId) {

        CustomResponse response = reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a specific review by its ID.
     * <p>
     * This endpoint allows users to get detailed information about a specific review.
     * </p>
     *
     * @param reviewId the ID of the review to retrieve
     * @return ResponseEntity containing the review details
     */
    @Operation(
        summary = "Get review by ID",
        description = "Retrieves detailed information about a specific review"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Review found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Review.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Review not found",
            content = @Content
        )
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewById(
        @Parameter(description = "ID of the review to retrieve", required = true)
        @PathVariable Long reviewId) {

        Review review = reviewService.findById(reviewId);
        return ResponseEntity.ok(review);
    }

    /**
     * Retrieves all reviews for a specific property.
     * <p>
     * This endpoint allows users to see all reviews for a particular property.
     * </p>
     *
     * @param propertyId the ID of the property whose reviews to retrieve
     * @return ResponseEntity containing a list of reviews for the property
     */
    @Operation(
        summary = "Get reviews by property ID",
        description = "Retrieves all reviews for a specific property"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of reviews retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Review.class))
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Property not found",
            content = @Content
        )
    })
    @GetMapping("/property/{propertyId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT','ROLE_HOST')")
    public ResponseEntity<List<Review>> getReviewsByPropertyId(
        @Parameter(description = "ID of the property whose reviews to retrieve", required = true)
        @PathVariable Long propertyId) {

        List<Review> reviews = reviewService.findByPropertyId(propertyId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Retrieves all reviews by a specific reviewer.
     * <p>
     * This endpoint allows users to see all reviews written by a particular reviewer.
     * </p>
     *
     * @param reviewerId the ID of the reviewer whose reviews to retrieve
     * @return ResponseEntity containing a list of reviews by the reviewer
     */
    @Operation(
        summary = "Get reviews by reviewer ID",
        description = "Retrieves all reviews written by a specific reviewer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of reviews retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Review.class))
            )
        )
    })
    @GetMapping("/reviewer/{reviewerId}")
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    public ResponseEntity<List<Review>> getReviewsByReviewerId(
        @Parameter(description = "ID of the reviewer whose reviews to retrieve", required = true)
        @PathVariable Long reviewerId) {

        List<Review> reviews = reviewService.findByReviewerId(reviewerId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Retrieves all reviews in the system.
     * <p>
     * This endpoint returns a list of all reviews available on the platform.
     * </p>
     *
     * @return ResponseEntity containing a list of all reviews
     */
    @Operation(
        summary = "Get all reviews",
        description = "Retrieves a list of all reviews available on the platform"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of reviews retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Review.class))
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.findAll();
        return ResponseEntity.ok(reviews);
    }

    /**
     * Adds a host response to a property review.
     * <p>
     * This endpoint allows property hosts to respond to reviews of their properties.
     * </p>
     *
     * @param reviewId the ID of the review to respond to
     * @param hostResponseRequest DTO containing the host's response
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Add host response to a review",
        description = "Allows property hosts to respond to reviews of their properties"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Response added successfully",
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
            responseCode = "403",
            description = "Forbidden - User is not the host of the property",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Review not found",
            content = @Content
        )
    })
    @PostMapping("/{reviewId}/host-response")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<CustomResponse> addHostResponse(
        @Parameter(description = "ID of the review to respond to", required = true)
        @PathVariable Long reviewId,

        @Parameter(description = "Host response details", required = true)
        @Valid @RequestBody HostResponseRequest hostResponseRequest) {

        CustomResponse response = reviewService.addHostResponse(reviewId, hostResponseRequest);
        return ResponseEntity.ok(response);
    }
}
