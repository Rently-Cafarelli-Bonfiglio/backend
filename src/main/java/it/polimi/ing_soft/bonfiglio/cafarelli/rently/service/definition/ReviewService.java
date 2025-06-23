package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.HostResponseRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ReviewCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ReviewUpdateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Review;

import java.util.List;

/**
 * This interface defines the contract for review services.
 * It includes methods for creating, updating, deleting, and retrieving reviews.
 */
public interface ReviewService {
    /**
     * Creates a new review for a property
     * @param propertyId the ID of the property being reviewed
     * @param reviewCreateRequest the review details
     * @return a custom response indicating the result of the operation
     */
    CustomResponse createPropertyReview(Long propertyId, ReviewCreateRequest reviewCreateRequest);

    /**
     * Creates a new review for a User
     * @param reviewedUserId the ID of the property being reviewed
     * @param reviewCreateRequest the review details
     * @return a custom response indicating the result of the operation
     */
    CustomResponse createUserReview(Long reviewedUserId, ReviewCreateRequest reviewCreateRequest);

    /**
     * Updates an existing review
     * @param reviewId the ID of the review to update
     * @param reviewUpdateRequest the updated review details
     * @return a custom response indicating the result of the operation
     */
    CustomResponse updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest);

    /**
     * Deletes a review
     * @param reviewId the ID of the review to delete
     * @return a custom response indicating the result of the operation
     */
    CustomResponse deleteReview(Long reviewId);

    /**
     * Retrieves a review by its ID
     * @param reviewId the ID of the review to retrieve
     * @return the review
     */
    Review findById(Long reviewId);

    /**
     * Retrieves all reviews for a specific property
     * @param propertyId the ID of the property
     * @return a list of reviews for the property
     */
    List<Review> findByPropertyId(Long propertyId);

    /**
     * Retrieves all reviews for a specific user
     * @param reviewedUserId the ID of the property
     * @return a list of reviews for the property
     */
    List<Review> findByReviewedUserId(Long reviewedUserId);

    /**
     * Retrieves all reviews by a specific reviewer
     * @param reviewerId the ID of the reviewer
     * @return a list of reviews by the reviewer
     */
    List<Review> findByReviewerId(Long reviewerId);

    /**
     * Retrieves all reviews in the system
     * @return a list of all reviews
     */
    List<Review> findAll();

    /**
     * Adds a host response to a property review
     * @param reviewId the ID of the review to respond to
     * @param hostResponseRequest the host's response details
     * @return a custom response indicating the result of the operation
     */
    CustomResponse addHostResponse(Long reviewId, HostResponseRequest hostResponseRequest);
}
