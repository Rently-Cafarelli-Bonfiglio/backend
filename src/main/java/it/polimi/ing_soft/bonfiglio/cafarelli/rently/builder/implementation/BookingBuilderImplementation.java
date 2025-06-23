package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.BookingBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Implementation of the {@link BookingBuilder} interface for building {@link Booking} objects.
 */
// This class uses the Builder design pattern to create instances of Booking.
@NoArgsConstructor
public class BookingBuilderImplementation implements BookingBuilder {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numOfAdults;
    private int numOfChildren;
    private BigDecimal total;
    private String bookingConfirmationCode;
    private User user;
    private Property property;

    public BookingBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public BookingBuilder checkInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
        return this;
    }
    @Override
    public BookingBuilder checkOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
        return this;
    }

    @Override
    public BookingBuilder numOfAdults(int numOfAdults) {
        this.numOfAdults = numOfAdults;
        return this;
    }
    @Override
    public BookingBuilder numOfChildren(int numOfChildren) {
        this.numOfChildren = numOfChildren;
        return this;
    }

    @Override
    public BookingBuilder total(BigDecimal total) {
        this.total = total;
        return this;
    }

    @Override
    public BookingBuilder bookingConfirmationCode(String bookingConfirmationCode) {
        this.bookingConfirmationCode = bookingConfirmationCode;
        return this;
    }
    @Override
    public BookingBuilder user(User user) {
        this.user = user;
        return this;
    }
    @Override
    public BookingBuilder property(Property property) {
        this.property = property;
        return this;
    }
    @Override
    public Booking build() {
        return new Booking(id, checkInDate, checkOutDate, numOfAdults, numOfChildren, total, bookingConfirmationCode, user, property);
    }
}
