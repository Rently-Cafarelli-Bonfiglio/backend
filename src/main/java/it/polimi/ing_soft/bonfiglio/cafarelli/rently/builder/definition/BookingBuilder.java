package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface for building {@link Booking} objects.
 */
public interface BookingBuilder {

    BookingBuilder id(Long iId);

    BookingBuilder checkInDate(LocalDate checkInDate);

    BookingBuilder checkOutDate(LocalDate checkOutDate);

    BookingBuilder numOfAdults(int numOfAdults);

    BookingBuilder numOfChildren(int numOfChildren);

    BookingBuilder total(BigDecimal total);

    BookingBuilder bookingConfirmationCode(String bookingConfirmationCode);

    BookingBuilder user(User user);

    BookingBuilder property(Property property);

    Booking build();
}
