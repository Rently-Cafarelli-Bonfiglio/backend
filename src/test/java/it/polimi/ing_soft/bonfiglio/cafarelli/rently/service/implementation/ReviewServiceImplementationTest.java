package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplementationTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ReviewServiceImplementation reviewService;

    private User client;
    private User host;
    private Property property;
    private Booking booking;
    private Review propertyReview;
    private Review userReview;
    private ReviewCreateRequest createRequest;
    private ReviewUpdateRequest updateRequest;
    private HostResponseRequest hostResponseRequest;

    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup client user (usa oggetti normali, non spy)
        client = new User();
        client.setId(1L);
        client.setUsername("client");
        client.setRole(Role.CLIENT);

        // Setup host user (usa oggetti normali, non spy)
        host = new User();
        host.setId(2L);
        host.setUsername("host");
        host.setRole(Role.HOST);

        // Setup property
        property = new Property();
        property.setId(1L);
        property.setTitle("Test Property");
        property.setHost(host);

        // Setup booking
        booking = new Booking();
        booking.setId(1L);
        booking.setProperty(property);
        booking.setUser(client);
        booking.setCheckInDate(LocalDate.now().minusDays(10));
        booking.setCheckOutDate(LocalDate.now().minusDays(5));

        // Setup property review
        propertyReview = new Review();
        propertyReview.setId(1L);
        propertyReview.setTitle("Great property");
        propertyReview.setDescription("Had a wonderful stay");
        propertyReview.setRating(5);
        propertyReview.setReviewer(client);
        propertyReview.setProperty(property);
        propertyReview.setReviewedUser(null);
        propertyReview.setCreatedAt(LocalDateTime.now());

        // Setup user review
        userReview = new Review();
        userReview.setId(2L);
        userReview.setTitle("Great host");
        userReview.setDescription("Very helpful");
        userReview.setRating(5);
        userReview.setReviewer(client);
        userReview.setProperty(null);
        userReview.setReviewedUser(host);
        userReview.setCreatedAt(LocalDateTime.now());

        // Setup create request
        createRequest = new ReviewCreateRequest();
        createRequest.setTitle("Test Review");
        createRequest.setDescription("Test Description");
        createRequest.setRating(4);

        // Setup update request
        updateRequest = new ReviewUpdateRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setRating(3);

        // Setup host response request
        hostResponseRequest = new HostResponseRequest();
        hostResponseRequest.setResponse("Thank you for your review");

        // Configurazione corretta dell'autenticazione
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);

        // IMPORTANTE: configura il principal per restituire l'oggetto User direttamente
        when(authentication.getPrincipal()).thenReturn(client);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Imposta il SecurityContext
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createPropertyReview_success() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(reviewRepository.findByReviewerIdAndPropertyId(1L, 1L)).thenReturn(Optional.empty());
        
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(bookingRepository.findByUserId(1L)).thenReturn(bookings);
        
        // Act
        CustomResponse response = reviewService.createPropertyReview(1L, createRequest);
        
        // Assert
        assertEquals("Review submitted successfully", response.getMessage());
        verify(reviewRepository).save(any(Review.class));
    }
    
    @Test
    void createPropertyReview_alreadyReviewed_returnsErrorMessage() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(reviewRepository.findByReviewerIdAndPropertyId(1L, 1L)).thenReturn(Optional.of(propertyReview));
        
        // Act
        CustomResponse response = reviewService.createPropertyReview(1L, createRequest);
        
        // Assert
        assertEquals("You have already reviewed this property", response.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }
    
    @Test
    void createPropertyReview_noBooking_throwsException() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(reviewRepository.findByReviewerIdAndPropertyId(1L, 1L)).thenReturn(Optional.empty());
        when(bookingRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        
        // Act & Assert
        assertThrows(UserUnauthorizedException.class, () -> 
            reviewService.createPropertyReview(1L, createRequest)
        );
        verify(reviewRepository, never()).save(any(Review.class));
    }
    
    @Test
    void createPropertyReview_futureBooking_throwsException() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(reviewRepository.findByReviewerIdAndPropertyId(1L, 1L)).thenReturn(Optional.empty());
        
        Booking futureBooking = new Booking();
        futureBooking.setProperty(property);
        futureBooking.setCheckOutDate(LocalDate.now().plusDays(5));
        
        List<Booking> bookings = new ArrayList<>();
        bookings.add(futureBooking);
        when(bookingRepository.findByUserId(1L)).thenReturn(bookings);
        
        // Act & Assert
        assertThrows(UserUnauthorizedException.class, () -> 
            reviewService.createPropertyReview(1L, createRequest)
        );
        verify(reviewRepository, never()).save(any(Review.class));
    }
    
    @Test
    void createUserReview_success() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(host));
        when(reviewRepository.findByReviewerIdAndReviewedUserId(1L, 2L)).thenReturn(Optional.empty());
        when(bookingRepository.existsBookingByHostIdAndClientId(client, host)).thenReturn(false);
        
        // Act
        CustomResponse response = reviewService.createUserReview(2L, createRequest);
        
        // Assert
        assertEquals("Review submitted successfully", response.getMessage());
        verify(reviewRepository).save(any(Review.class));
    }
    
    @Test
    void createUserReview_clientToClient_returnsErrorMessage() {
        // Arrange
        User otherClient = new User();
        otherClient.setId(3L);
        otherClient.setRole(Role.CLIENT);
        
        when(userRepository.findById(3L)).thenReturn(Optional.of(otherClient));
        
        // Act
        CustomResponse response = reviewService.createUserReview(3L, createRequest);
        
        // Assert
        assertEquals("You can't review another customer", response.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createUserReview_hostToHost_returnsErrorMessage() {
        // Arrange
        // Riconfigura l'autenticazione per l'host
        when(authentication.getPrincipal()).thenReturn(host);

        User otherHost = new User();
        otherHost.setId(3L);
        otherHost.setRole(Role.HOST);

        when(userRepository.findById(3L)).thenReturn(Optional.of(otherHost));

        // Act
        CustomResponse response = reviewService.createUserReview(3L, createRequest);

        // Assert
        assertEquals("You can't review another host", response.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createUserReview_alreadyReviewed_returnsErrorMessage() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(host));
        when(reviewRepository.findByReviewerIdAndReviewedUserId(1L, 2L)).thenReturn(Optional.of(userReview));
        
        // Act
        CustomResponse response = reviewService.createUserReview(2L, createRequest);
        
        // Assert
        assertEquals("You have already reviewed this User", response.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }
    
    @Test
    void updateReview_success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(propertyReview));
        
        // Act
        CustomResponse response = reviewService.updateReview(1L, updateRequest);
        
        // Assert
        assertEquals("Review updated successfully", response.getMessage());
        assertEquals("Updated Title", propertyReview.getTitle());
        assertEquals("Updated Description", propertyReview.getDescription());
        assertEquals(3, propertyReview.getRating());
        verify(reviewRepository).save(propertyReview);
    }

    @Test
    void updateReview_notAuthor_throwsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(3L);

        // Riconfigura l'autenticazione per l'altro utente
        when(authentication.getPrincipal()).thenReturn(otherUser);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(propertyReview));

        // Act & Assert
        assertThrows(UserUnauthorizedException.class, () ->
                reviewService.updateReview(1L, updateRequest)
        );
        verify(reviewRepository, never()).save(any(Review.class));
    }

    
    @Test
    void deleteReview_asAuthor_success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(propertyReview));
        
        // Act
        CustomResponse response = reviewService.deleteReview(1L);
        
        // Assert
        assertEquals("Review deleted successfully", response.getMessage());
        verify(reviewRepository).delete(propertyReview);
    }
    
    @Test
    void deleteReview_asPropertyOwner_success() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(host, null)
        );
        
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(propertyReview));
        
        // Act
        CustomResponse response = reviewService.deleteReview(1L);
        
        // Assert
        assertEquals("Review deleted successfully", response.getMessage());
        verify(reviewRepository).delete(propertyReview);
    }

    @Test
    void deleteReview_unauthorized_throwsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(3L);

        // Riconfigura l'autenticazione per l'altro utente
        when(authentication.getPrincipal()).thenReturn(otherUser);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(propertyReview));

        // Act & Assert
        assertThrows(UserUnauthorizedException.class, () ->
                reviewService.deleteReview(1L)
        );
        verify(reviewRepository, never()).delete(any(Review.class));
    }
    @Test
    void findById_success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(propertyReview));
        
        // Act
        Review result = reviewService.findById(1L);
        
        // Assert
        assertEquals(propertyReview, result);
    }
    
    @Test
    void findById_notFound_throwsException() {
        // Arrange
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            reviewService.findById(999L)
        );
    }
    
    @Test
    void findByPropertyId_success() {
        // Arrange
        List<Review> reviews = Arrays.asList(propertyReview);
        when(propertyRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findByPropertyId(1L)).thenReturn(reviews);
        
        // Act
        List<Review> result = reviewService.findByPropertyId(1L);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(propertyReview, result.get(0));
    }
    
    @Test
    void findByPropertyId_propertyNotFound_throwsException() {
        // Arrange
        when(propertyRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            reviewService.findByPropertyId(999L)
        );
    }
    
    @Test
    void findByReviewedUserId_success() {
        // Arrange
        List<Review> reviews = Arrays.asList(userReview);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(reviewRepository.findByReviewedUserId(2L)).thenReturn(reviews);
        
        // Act
        List<Review> result = reviewService.findByReviewedUserId(2L);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(userReview, result.get(0));
    }
    
    @Test
    void findByReviewedUserId_userNotFound_throwsException() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            reviewService.findByReviewedUserId(999L)
        );
    }
    
    @Test
    void findByReviewerId_success() {
        // Arrange
        List<Review> reviews = Arrays.asList(propertyReview, userReview);
        when(reviewRepository.findByReviewerId(1L)).thenReturn(reviews);
        
        // Act
        List<Review> result = reviewService.findByReviewerId(1L);
        
        // Assert
        assertEquals(2, result.size());
    }
    
    @Test
    void findAll_success() {
        // Arrange
        List<Review> reviews = Arrays.asList(propertyReview, userReview);
        when(reviewRepository.findAll()).thenReturn(reviews);
        
        // Act
        List<Review> result = reviewService.findAll();
        
        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void addHostResponse_success() {
        // Arrange
        // Riconfigura l'autenticazione per l'host
        when(authentication.getPrincipal()).thenReturn(host);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(propertyReview));

        // Act
        CustomResponse response = reviewService.addHostResponse(1L, hostResponseRequest);

        // Assert
        assertEquals("Response added successfully", response.getMessage());
        assertEquals("Thank you for your review", propertyReview.getHostResponse());
        assertNotNull(propertyReview.getHostResponseCreatedAt());
        verify(reviewRepository).save(propertyReview);
    }
    @Test
    void addHostResponse_notPropertyReview_returnsErrorMessage() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(host, null)
        );
        
        when(reviewRepository.findById(2L)).thenReturn(Optional.of(userReview));
        
        // Act
        CustomResponse response = reviewService.addHostResponse(2L, hostResponseRequest);
        
        // Assert
        assertEquals("This review is not for a property", response.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }
    
    @Test
    void addHostResponse_notPropertyOwner_throwsException() {
        // Arrange
        User otherHost = new User();
        otherHost.setId(3L);
        otherHost.setRole(Role.HOST);
        
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(otherHost, null)
        );
        
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(propertyReview));
        
        // Act & Assert
        assertThrows(UserUnauthorizedException.class, () -> 
            reviewService.addHostResponse(1L, hostResponseRequest)
        );
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void addHostResponse_alreadyResponded_returnsErrorMessage() {
        // Arrange
        // Riconfigura l'autenticazione per l'host
        when(authentication.getPrincipal()).thenReturn(host);

        propertyReview.setHostResponse("Existing response");
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(propertyReview));

        // Act
        CustomResponse response = reviewService.addHostResponse(1L, hostResponseRequest);

        // Assert
        assertEquals("You have already responded to this review", response.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }
}