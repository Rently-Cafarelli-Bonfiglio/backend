package it.polimi.ing_soft.bonfiglio.cafarelli.rently.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This class represents a booking made by a user for a property.
 * It contains information about the booking such as check-in and check-out dates,
 * number of adults and children, total price, confirmation code, user and property details.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Booking implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Future(message = "check in date must be in the future")
    @NotNull(message = "check in date is required")
    private LocalDate checkInDate;

    @Future(message = "check out date must be in the future")
    @NotNull(message = "check out date is required")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Number of adults must not be less that 1")
    private int numOfAdults;

    @Min(value = 0, message = "Number of children must not be less that 0")
    private int numOfChildren;

    private BigDecimal total;

    private String bookingConfirmationCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;


}