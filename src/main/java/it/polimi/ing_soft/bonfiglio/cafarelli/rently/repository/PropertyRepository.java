package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Property entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    /**
     * Finds all the Properties by his owner.
     *
     * @param hostId the ID of the host
     * @return an Optional containing the Property if found, or empty if not found
     */
    List<Property> findByHostId(Long hostId);

    /**
     * Finds a Property by its title.
     *
     * @param title the title of the property
     * @return an Optional containing the Property if found, or empty if not found
     */

    Optional<Property> findByTitle(String title);

    /**
     * Finds a Property by its title and host ID.
     *
     * @param title the title of the property
     * @param hostId the ID of the host
     * @return an Optional containing the Property if found, or empty if not found
     */

    Optional<Property> findByTitleAndHostId(String title, Long hostId);

    /**
     * Finds all Properties located in a specific city.
     *
     * @param city the city where the properties are located
     * @return a List of Properties located in the specified city
     */

    List<Property> findByCity(String city);

    /**
     * Finds all Properties that are currently available.
     *
     * @return a List of Properties that are available
     */

    List<Property> findByIsAvailableIsTrue();
    @Query("SELECT DISTINCT p FROM Property p " +
            "WHERE p.city = :city " +
            "AND p.maxGuests >= :numberOfGuests " +
            "AND p.isAvailable = true " +
            "AND p.id NOT IN (" +
            "    SELECT b.property.id FROM Booking b " +
            "    WHERE (:checkInDate < b.checkOutDate) " +
            "    AND (:checkOutDate > b.checkInDate)" +
            ")")
    /**
     * Finds all available properties in a specific city
     * that can accommodate a given number of guests
     * and are not booked during the specified date range.
     * @param city the city where the properties are located
     * @param checkInDate the check-in date for the booking
     * @param checkOutDate the check-out date for the booking
     * @param numberOfGuests the number of guests for the booking
     * @return a List of Properties that are available, meet the guest requirements,
     */
    List<Property> findAvailableProperties(
            @Param("city") String city,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("numberOfGuests") Integer numberOfGuests
    );
}