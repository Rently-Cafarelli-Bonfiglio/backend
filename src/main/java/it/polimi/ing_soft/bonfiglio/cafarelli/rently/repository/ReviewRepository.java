package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Review;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Review entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * Finds all reviews made by a specific reviewer.
     *
     * @param reviewerId the ID of the reviewer
     * @return a list of reviews made by the specified reviewer
     */
    List<Review> findByReviewerId(Long reviewerId);

    /**
     * Finds all reviews for a specific property.
     *
     * @param propertyId the ID of the property
     * @return a list of reviews for the specified property
     */

    List<Review> findByPropertyId(Long propertyId);

    /**
     * Finds a review made by a specific reviewer for a specific property.
     *
     * @param reviewerId the ID of the reviewer
     * @param propertyId the ID of the property
     * @return an Optional containing the review if found, or empty if not found
     */

    Optional<Review> findByReviewerIdAndPropertyId(Long reviewerId, Long propertyId);

    /**
     * Finds all reviews made for a specific user.
     *
     * @param reviewedUser the user who is being reviewed
     * @return a list of reviews made for the specified user
     */

    List<Review> findByReviewedUser(User reviewedUser);

    /**
     * Finds a review made by a specific reviewer for a specific user.
     *
     * @param reviewerId the ID of the reviewer
     * @param reviewedUserId the ID of the user being reviewed
     * @return an Optional containing the review if found, or empty if not found
     */

    Optional<Review> findByReviewerIdAndReviewedUserId(Long reviewerId, Long reviewedUserId);

    /**
     * Finds all reviews for a specific user by their ID.
     *
     * @param reviewedUserId the ID of the user being reviewed
     * @return a list of reviews for the specified user
     */
    List<Review> findByReviewedUserId(Long reviewedUserId);

}
