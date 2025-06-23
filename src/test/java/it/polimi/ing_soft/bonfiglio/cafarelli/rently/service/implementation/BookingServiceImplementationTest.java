package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.BookingCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.BookingDashboardResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.PaymentRejectedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UnavailablePropertyException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UserUnauthorizedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Role;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.EventManager;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.BookingRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.CouponService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplementationTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CouponService couponService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private EventManager eventManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookingServiceImplementation bookingService;

    private User customer;
    private User host;
    private Property property;
    private Booking booking;
    private BookingCreateRequest bookingRequest;

    private static final Long CUSTOMER_ID = 1L;
    private static final Long HOST_ID = 2L;
    private static final Long PROPERTY_ID = 3L;
    private static final Long BOOKING_ID = 4L;
    private static final String CONFIRMATION_CODE = "ABC1234567";
    private static final String COUPON_CODE = "DISCOUNT20";
    private static final BigDecimal PRICE_PER_NIGHT = new BigDecimal("100.00");
    private static final BigDecimal TOTAL_AMOUNT = new BigDecimal("300.00");
    private static final LocalDate CHECK_IN = LocalDate.of(2025, 7, 1);
    private static final LocalDate CHECK_OUT = LocalDate.of(2025, 7, 4);

    @BeforeEach
    void setUp() {
        // Setup Customer
        customer = new User();
        customer.setId(CUSTOMER_ID);
        customer.setUsername("customer");
        customer.setFirstname("John");
        customer.setLastname("Doe");
        customer.setEmail("john@example.com");
        customer.setBalance(new BigDecimal("500.00"));
        customer.setRole(Role.CLIENT);

        // Setup Host
        host = new User();
        host.setId(HOST_ID);
        host.setUsername("host");
        host.setFirstname("Jane");
        host.setLastname("Smith");
        host.setBalance(new BigDecimal("1000.00"));
        host.setRole(Role.HOST);

        // Setup Property
        property = new Property();
        property.setId(PROPERTY_ID);
        property.setTitle("Beautiful Apartment");
        property.setHost(host);
        property.setPricePerNight(PRICE_PER_NIGHT);
        property.setMaxGuests(4);

        // Setup Booking
        booking = new Booking();
        booking.setId(BOOKING_ID);
        booking.setUser(customer);
        booking.setProperty(property);
        booking.setCheckInDate(CHECK_IN);
        booking.setCheckOutDate(CHECK_OUT);
        booking.setNumOfAdults(2);
        booking.setNumOfChildren(1);
        booking.setTotal(TOTAL_AMOUNT);
        booking.setBookingConfirmationCode(CONFIRMATION_CODE);

        // Setup BookingCreateRequest
        bookingRequest = new BookingCreateRequest();
        bookingRequest.setProperty(property);
        bookingRequest.setCheckInDate(CHECK_IN);
        bookingRequest.setCheckOutDate(CHECK_OUT);
        bookingRequest.setNumOfAdults(2);
        bookingRequest.setNumOfChildren(1);
        bookingRequest.setCouponCode(COUPON_CODE);
    }

    @Test
    void saveBooking_WithCoupon_Success() throws Exception {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(customer);

            when(bookingRepository.existsOverlappingBooking(PROPERTY_ID, CHECK_IN, CHECK_OUT)).thenReturn(false);
            when(paymentService.payForAccomodation(host.getUsername(), customer.getUsername(), TOTAL_AMOUNT, COUPON_CODE)).thenReturn(true);
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            // When
            CustomResponse result = bookingService.saveBooking(bookingRequest);

            // Then
            assertEquals("Booking confirmed", result.getMessage());
            verify(bookingRepository).save(any(Booking.class));
            verify(couponService).assertUsedCoupon(CUSTOMER_ID, COUPON_CODE);
            verify(eventManager).notify(eq("BOOKING_CREATED"), any(Booking.class));
        }
    }

    @Test
    void saveBooking_WithoutCoupon_Success() throws Exception {
        // Given
        bookingRequest.setCouponCode(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(customer);

            when(bookingRepository.existsOverlappingBooking(PROPERTY_ID, CHECK_IN, CHECK_OUT)).thenReturn(false);
            when(paymentService.payForAccomodation(host.getUsername(), customer.getUsername(), TOTAL_AMOUNT, null)).thenReturn(true);
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            // When
            CustomResponse result = bookingService.saveBooking(bookingRequest);

            // Then
            assertEquals("Booking confirmed", result.getMessage());
            verify(bookingRepository).save(any(Booking.class));
            verify(couponService, never()).assertUsedCoupon(any(), any());
            verify(eventManager).notify(eq("BOOKING_CREATED"), any(Booking.class));
        }
    }

    @Test
    void saveBooking_InvalidDates_ThrowsException() {
        // Given
        bookingRequest.setCheckInDate(CHECK_OUT);
        bookingRequest.setCheckOutDate(CHECK_IN);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(customer);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> bookingService.saveBooking(bookingRequest));
        }
    }

    @Test
    void saveBooking_PropertyUnavailable_ThrowsException() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(customer);

            when(bookingRepository.existsOverlappingBooking(PROPERTY_ID, CHECK_IN, CHECK_OUT)).thenReturn(true);

            // When & Then
            assertThrows(UnavailablePropertyException.class, () -> bookingService.saveBooking(bookingRequest));
        }
    }

    @Test
    void saveBooking_TooManyGuests_ThrowsException() {
        // Given
        bookingRequest.setNumOfAdults(3);
        bookingRequest.setNumOfChildren(2); // Totale 5, max 4

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(customer);

            when(bookingRepository.existsOverlappingBooking(PROPERTY_ID, CHECK_IN, CHECK_OUT)).thenReturn(false);

            // When & Then
            assertThrows(UnavailablePropertyException.class, () -> bookingService.saveBooking(bookingRequest));
        }
    }

    @Test
    void saveBooking_PaymentFailed_ThrowsException() throws Exception {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(customer);

            when(bookingRepository.existsOverlappingBooking(PROPERTY_ID, CHECK_IN, CHECK_OUT)).thenReturn(false);
            when(paymentService.payForAccomodation(host.getUsername(), customer.getUsername(), TOTAL_AMOUNT, COUPON_CODE)).thenReturn(false);

            // When & Then
            assertThrows(PaymentRejectedException.class, () -> bookingService.saveBooking(bookingRequest));
            verify(bookingRepository, never()).save(any());
            verify(couponService, never()).assertUsedCoupon(any(), any());
        }
    }

    @Test
    void findBookingByConfirmationCode_Success() {
        // Given
        when(bookingRepository.findByBookingConfirmationCode(CONFIRMATION_CODE)).thenReturn(Optional.of(booking));

        // When
        Booking result = bookingService.findBookingByConfirmationCode(CONFIRMATION_CODE);

        // Then
        assertEquals(booking, result);
        verify(bookingRepository).findByBookingConfirmationCode(CONFIRMATION_CODE);
    }

    @Test
    void findBookingByConfirmationCode_NotFound_ThrowsException() {
        // Given
        when(bookingRepository.findByBookingConfirmationCode(CONFIRMATION_CODE)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                bookingService.findBookingByConfirmationCode(CONFIRMATION_CODE));
    }

    @Test
    void getAllBookings_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getAllBookings();

        // Then
        assertEquals(bookings, result);
        verify(bookingRepository).findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Test
    void getAllBookingsByUserId_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findByUserId(CUSTOMER_ID)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getAllBookings(CUSTOMER_ID);

        // Then
        assertEquals(bookings, result);
        verify(bookingRepository).findByUserId(CUSTOMER_ID);
    }

    @Test
    void cancelBooking_ByOwner_Success() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(customer);

            when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

            // When
            CustomResponse result = bookingService.cancelBooking(BOOKING_ID);

            // Then
            assertEquals("Prenotazione cancellata con successo", result.getMessage());
            verify(userRepository, times(2)).save(any(User.class));
            verify(bookingRepository).delete(booking);
            verify(eventManager).notify("BOOKING_CANCELED", booking);

            // Verifica aggiornamento saldi
            assertEquals(new BigDecimal("800.00"), customer.getBalance()); // 500 + 300
            assertEquals(new BigDecimal("700.00"), host.getBalance()); // 1000 - 300
        }
    }

    @Test
    void cancelBooking_ByAdmin_Success() {
        // Given
        User admin = new User();
        admin.setId(999L);
        admin.setRole(Role.ADMIN);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(admin);

            when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

            // When
            CustomResponse result = bookingService.cancelBooking(BOOKING_ID);

            // Then
            assertEquals("Prenotazione cancellata con successo", result.getMessage());
            verify(bookingRepository).delete(booking);
        }
    }

    @Test
    void cancelBooking_ByModerator_Success() {
        // Given
        User moderator = new User();
        moderator.setId(998L);
        moderator.setRole(Role.MODERATOR);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(moderator);

            when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

            // When
            CustomResponse result = bookingService.cancelBooking(BOOKING_ID);

            // Then
            assertEquals("Prenotazione cancellata con successo", result.getMessage());
            verify(bookingRepository).delete(booking);
        }
    }

    @Test
    void cancelBooking_Unauthorized_ThrowsException() {
        // Given
        User otherUser = new User();
        otherUser.setId(999L);
        otherUser.setRole(Role.CLIENT);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(otherUser);

            when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

            // When & Then
            assertThrows(UserUnauthorizedException.class, () -> bookingService.cancelBooking(BOOKING_ID));
            verify(bookingRepository, never()).delete(any());
        }
    }

    @Test
    void cancelBooking_BookingNotFound_ThrowsException() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(customer);

            when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(EntityNotFoundException.class, () -> bookingService.cancelBooking(BOOKING_ID));
        }
    }

    @Test
    void getAllBookingsByHostId_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findByProperty_Host_Id(HOST_ID)).thenReturn(bookings);

        // When
        List<BookingDashboardResponse> result = bookingService.getAllBookingsByHostId(HOST_ID);

        // Then
        assertEquals(1, result.size());
        BookingDashboardResponse response = result.get(0);
        assertEquals("Beautiful Apartment", response.getTitle());
        assertEquals(CUSTOMER_ID, response.getUser().getId());
        assertEquals("John", response.getUser().getFirstname());
        assertEquals("Doe", response.getUser().getLastname());
        assertEquals("john@example.com", response.getUser().getEmail());
        assertEquals(CHECK_IN, response.getCheckInDate());
        assertEquals(CHECK_OUT, response.getCheckOutDate());
        assertEquals(TOTAL_AMOUNT, response.getTotal());

        verify(bookingRepository).findByProperty_Host_Id(HOST_ID);
    }

    @Test
    void getAllBookingsByHostId_EmptyList_Success() {
        // Given
        when(bookingRepository.findByProperty_Host_Id(HOST_ID)).thenReturn(Arrays.asList());

        // When
        List<BookingDashboardResponse> result = bookingService.getAllBookingsByHostId(HOST_ID);

        // Then
        assertTrue(result.isEmpty());
        verify(bookingRepository).findByProperty_Host_Id(HOST_ID);
    }
}