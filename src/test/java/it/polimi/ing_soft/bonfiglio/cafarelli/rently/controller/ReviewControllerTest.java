package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.HostResponseRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ReviewCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ReviewUpdateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Review;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPropertyReview_returnsCustomResponse() {
        Long propertyId = 1L;
        ReviewCreateRequest request = new ReviewCreateRequest();
        CustomResponse expectedResponse = new CustomResponse("Success");

        when(reviewService.createPropertyReview(propertyId, request)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = reviewController.createPropertyReview(propertyId, request);

        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void createUserReview_returnsCustomResponse() {
        Long userId = 2L;
        ReviewCreateRequest request = new ReviewCreateRequest();
        CustomResponse expectedResponse = new CustomResponse("Success");

        when(reviewService.createUserReview(userId, request)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = reviewController.createUserReview(userId, request);

        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void updateReview_returnsCustomResponse() {
        Long reviewId = 3L;
        ReviewUpdateRequest request = new ReviewUpdateRequest();
        CustomResponse expectedResponse = new CustomResponse("Updated");

        when(reviewService.updateReview(reviewId, request)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = reviewController.updateReview(reviewId, request);

        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void deleteReview_returnsCustomResponse() {
        Long reviewId = 4L;
        CustomResponse expectedResponse = new CustomResponse("Deleted");

        when(reviewService.deleteReview(reviewId)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = reviewController.deleteReview(reviewId);

        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getReviewById_returnsReview() {
        Long reviewId = 5L;
        Review expectedReview = new Review();

        when(reviewService.findById(reviewId)).thenReturn(expectedReview);

        ResponseEntity<Review> response = reviewController.getReviewById(reviewId);

        assertEquals(expectedReview, response.getBody());
    }

    @Test
    void getReviewsByPropertyId_returnsReviewList() {
        Long propertyId = 6L;
        List<Review> expectedReviews = List.of(new Review());

        when(reviewService.findByPropertyId(propertyId)).thenReturn(expectedReviews);

        ResponseEntity<List<Review>> response = reviewController.getReviewsByPropertyId(propertyId);

        assertEquals(expectedReviews, response.getBody());
    }

    @Test
    void getReviewsByReviewerId_returnsReviewList() {
        Long reviewerId = 7L;
        List<Review> expectedReviews = List.of(new Review());

        when(reviewService.findByReviewerId(reviewerId)).thenReturn(expectedReviews);

        ResponseEntity<List<Review>> response = reviewController.getReviewsByReviewerId(reviewerId);

        assertEquals(expectedReviews, response.getBody());
    }

    @Test
    void getAllReviews_returnsReviewList() {
        List<Review> expectedReviews = List.of(new Review());

        when(reviewService.findAll()).thenReturn(expectedReviews);

        ResponseEntity<List<Review>> response = reviewController.getAllReviews();

        assertEquals(expectedReviews, response.getBody());
    }

    @Test
    void addHostResponse_returnsCustomResponse() {
        Long reviewId = 8L;
        HostResponseRequest request = new HostResponseRequest();
        CustomResponse expectedResponse = new CustomResponse("Response added");

        when(reviewService.addHostResponse(reviewId, request)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = reviewController.addHostResponse(reviewId, request);

        assertEquals(expectedResponse, response.getBody());
    }
}
