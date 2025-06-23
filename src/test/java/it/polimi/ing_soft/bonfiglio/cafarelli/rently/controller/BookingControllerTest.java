package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.BookingCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.BookingDashboardResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveBooking_ShouldReturnCustomResponse() {
        BookingCreateRequest request = new BookingCreateRequest();
        CustomResponse mockResponse = new CustomResponse("Booking confirmed");

        when(bookingService.saveBooking(request)).thenReturn(mockResponse);

        ResponseEntity<CustomResponse> response = bookingController.saveBooking(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Booking confirmed", response.getBody().getMessage());
    }

    @Test
    void getAllBookings_Admin_ShouldReturnAllBookings() {
        List<Booking> mockBookings = List.of(new Booking(), new Booking());

        when(bookingService.getAllBookings()).thenReturn(mockBookings);

        ResponseEntity<List<Booking>> response = bookingController.getAllBookings();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllBookings_User_ShouldReturnBookingsForUser() {
        Long userId = 1L;
        List<Booking> mockBookings = List.of(new Booking(), new Booking());

        when(bookingService.getAllBookings(userId)).thenReturn(mockBookings);

        ResponseEntity<List<Booking>> response = bookingController.getAllBookings(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void cancelBooking_ShouldReturnSuccessResponse() {
        Long bookingId = 1L;
        CustomResponse mockResponse = new CustomResponse("Prenotazione cancellata con successo");

        when(bookingService.cancelBooking(bookingId)).thenReturn(mockResponse);

        ResponseEntity<CustomResponse> response = bookingController.cancelBooking(bookingId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Prenotazione cancellata con successo", response.getBody().getMessage());
    }

    @Test
    void getAllBookingsByHostId_ShouldReturnHostBookings() {
        Long hostId = 1L;
        BookingDashboardResponse booking1 = new BookingDashboardResponse("Casa Mare", null, LocalDate.now(), LocalDate.now().plusDays(2), BigDecimal.valueOf(150));
        BookingDashboardResponse booking2 = new BookingDashboardResponse("Villa Collina", null, LocalDate.now(), LocalDate.now().plusDays(3), BigDecimal.valueOf(300));

        List<BookingDashboardResponse> mockList = List.of(booking1, booking2);

        when(bookingService.getAllBookingsByHostId(hostId)).thenReturn(mockList);

        ResponseEntity<List<BookingDashboardResponse>> response = bookingController.getAllBookingsByHostId(hostId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Casa Mare", response.getBody().get(0).getTitle());
    }
}