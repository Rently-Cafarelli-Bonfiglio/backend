package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation.ReviewBuilderImplementation;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.HostResponseRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ReviewCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ReviewUpdateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UserUnauthorizedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.*;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.BookingRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.PropertyRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.ReviewRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.ReviewService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class implements the ReviewService interface and provides methods for managing reviews.
 * It includes validation to ensure users can only review properties they have booked and stayed at.
 */
@Service
@AllArgsConstructor
public class ReviewServiceImplementation implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CustomResponse createPropertyReview(Long propertyId, ReviewCreateRequest reviewCreateRequest) {
        User reviewer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException(Property.class));

        // Check if the user has already reviewed this property
        if (reviewRepository.findByReviewerIdAndPropertyId(reviewer.getId(), propertyId).isPresent()) {
            return new CustomResponse("You have already reviewed this property");
        }

        // Check if the user has a concluded booking for this property
        List<Booking> userBookings = bookingRepository.findByUserId(reviewer.getId());
        boolean hasConcludedBooking = userBookings.stream()
                .anyMatch(booking -> 
                    booking.getProperty().getId().equals(propertyId) &&
                    booking.getCheckOutDate().isBefore(LocalDate.now())
                );

        if (!hasConcludedBooking) {
            throw new UserUnauthorizedException("You can only review properties you have stayed at");
        }

        Review review = new ReviewBuilderImplementation()
                .title(reviewCreateRequest.getTitle())
                .description(reviewCreateRequest.getDescription())
                .rating(reviewCreateRequest.getRating())
                .reviewer(reviewer)
                .property(property)
                .reviewedUser(null)
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        return new CustomResponse("Review submitted successfully");
    }

    @Override
    public CustomResponse createUserReview(Long reviewedUserId, ReviewCreateRequest reviewCreateRequest){
        User reviewer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User reviewedUser = userRepository.findById(reviewedUserId)
                .orElseThrow(() -> new EntityNotFoundException(Property.class));

        if(reviewer.getRole()== Role.CLIENT && reviewedUser.getRole()==Role.CLIENT){
            return new CustomResponse("You can't review another customer");
        }

        if(reviewer.getRole()== Role.HOST && reviewedUser.getRole()==Role.HOST){
            return new CustomResponse("You can't review another host");
        }

        // Check if the user has already reviewed this user
        if (reviewRepository.findByReviewerIdAndReviewedUserId(reviewer.getId(), reviewedUserId).isPresent()) {
            return new CustomResponse("You have already reviewed this User");
        }

        if (bookingRepository.existsBookingByHostIdAndClientId(reviewer, reviewedUser)) {
            throw new IllegalArgumentException("You can review only customer who stayed in your properties");
        }

        Review review = new ReviewBuilderImplementation()
                .title(reviewCreateRequest.getTitle())
                .description(reviewCreateRequest.getDescription())
                .rating(reviewCreateRequest.getRating())
                .reviewer(reviewer)
                .property(null)
                .reviewedUser(reviewedUser)
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        return new CustomResponse("Review submitted successfully");

    }

    @Override
    @Transactional
    public CustomResponse updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest) {
        User reviewer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(Review.class));

        // Check if the user is the author of the review
        if (!review.getReviewer().getId().equals(reviewer.getId())) {
            throw new UserUnauthorizedException("You can only update your own reviews");
        }

        review.setTitle(reviewUpdateRequest.getTitle());
        review.setDescription(reviewUpdateRequest.getDescription());
        review.setRating(reviewUpdateRequest.getRating());

        reviewRepository.save(review);

        return new CustomResponse("Review updated successfully");
    }

    @Override
    @Transactional
    public CustomResponse deleteReview(Long reviewId) {
        User reviewer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(Review.class));

        // Check if the user is the author of the review or the property owner
        boolean isAuthor = review.getReviewer().getId().equals(reviewer.getId());
        boolean isPropertyOwner = review.getProperty().getHost().getId().equals(reviewer.getId());

        if (!isAuthor && !isPropertyOwner) {
            throw new UserUnauthorizedException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);

        return new CustomResponse("Review deleted successfully");
    }

    @Override
    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(Review.class));
    }

    @Override
    public List<Review> findByPropertyId(Long propertyId) {
        // Verify property exists
        if (!propertyRepository.existsById(propertyId)) {
            throw new EntityNotFoundException(Property.class);
        }

        return reviewRepository.findByPropertyId(propertyId);
    }

    @Override
    public List<Review> findByReviewedUserId(Long reviewedUserId) {
        // Verify user exists
        if (!userRepository.existsById(reviewedUserId)) {
            throw new EntityNotFoundException(User.class);
        }

        return reviewRepository.findByReviewedUserId(reviewedUserId);
    }

    @Override
    public List<Review> findByReviewerId(Long reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    @Transactional
    public CustomResponse addHostResponse(Long reviewId, HostResponseRequest hostResponseRequest) {
        User host = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Find the review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(Review.class));

        // Check if the review is for a property
        if (!review.isPropertyReview()) {
            return new CustomResponse("This review is not for a property");
        }

        // Check if the user is the host of the property
        if (!review.getProperty().getHost().getId().equals(host.getId())) {
            throw new UserUnauthorizedException("You can only respond to reviews for your own properties");
        }

        // Check if the host has already responded
        if (review.getHostResponse() != null && !review.getHostResponse().isEmpty()) {
            return new CustomResponse("You have already responded to this review");
        }

        // Add the host's response
        review.setHostResponse(hostResponseRequest.getResponse());
        review.setHostResponseCreatedAt(LocalDateTime.now());

        // Save the updated review
        reviewRepository.save(review);

        return new CustomResponse("Response added successfully");
    }
}
