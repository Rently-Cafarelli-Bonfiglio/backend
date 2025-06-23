package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Booking entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds a Booking by its confirmation code.
     *
     * @param confirmationCode the confirmation code of the booking
     * @return an Optional containing the Booking if found, or empty if not found
     */

    Optional<Booking> findByBookingConfirmationCode(String confirmationCode);

    /**
     * Finds all Bookings associated with a specific user.
     *
     * @param userId the ID of the user
     * @return a List of Bookings associated with the user
     */

    List<Booking> findByUserId(Long userId);


    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "JOIN b.property p " +
            "WHERE p.host = :host " +
            "AND b.user = :customer ")

    /**
     * Checks if a booking exists for a given host and customer.
     *
     * @param host the User who is the host
     * @param customer the User who is the customer
     * @return true if a booking exists, false otherwise
     */

    Boolean existsBookingByHostIdAndClientId(@Param("host") User host,
                                             @Param("customer") User customer
                                             );

    Booking findByCheckInDateOrCheckOutDate(LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.property.id = :propertyId " +
           "AND (" +
           "   :checkInDate = b.checkInDate " +
           "   OR :checkOutDate = b.checkOutDate " +
           "   OR (:checkInDate > b.checkInDate AND :checkInDate < b.checkOutDate) " +
           "   OR (:checkOutDate > b.checkInDate AND :checkOutDate < b.checkOutDate) " +
           "   OR (:checkInDate < b.checkInDate AND :checkOutDate > b.checkOutDate)" +
           ")")
    boolean existsOverlappingBooking(@Param("propertyId") Long propertyId, 
                                    @Param("checkInDate") LocalDate checkInDate, 
                                    @Param("checkOutDate") LocalDate checkOutDate);

    List<Booking> findByProperty_Host_Id(Long hostId);

    boolean existsByPropertyId(Long propertyId);


}
