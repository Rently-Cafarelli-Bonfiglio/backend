package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.BookingCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.BookingDashboardResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;

import java.util.List;

/**
 * This interface defines the contract for booking services.
 * It includes methods for creating, retrieving, and canceling bookings.
 */
public interface BookingService {
    /**
     * Saves a booking for a specific room and user.
     *
     * @param bookingRequest the request containing booking details
     * @return a CustomResponse indicating the result of the booking operation
     */

   CustomResponse saveBooking(BookingCreateRequest bookingRequest);

    /**
     * Finds a booking by its confirmation code.
     *
     * @param confirmationCode the confirmation code of the booking
     * @return the Booking object if found, otherwise null
     */

   Booking findBookingByConfirmationCode(String confirmationCode);

    /**
     * Retrieves all bookings.
     *
     * @return a list of all Booking objects
     */

   List<Booking> getAllBookings();

    /**
     * Retrieves all bookings for a specific user.
     *
     * @param userId the ID of the user whose bookings are to be retrieved
     * @return a list of Booking objects for the specified user
     */

   List<Booking> getAllBookings(Long userId);

    /**
     * Cancels a booking by its ID.
     *
     * @param bookingId the ID of the booking to cancel
     * @return a CustomResponse indicating the result of the cancellation operation
     */

   CustomResponse cancelBooking(Long bookingId);

   List<BookingDashboardResponse> getAllBookingsByHostId(Long hostId);
}
