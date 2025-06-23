package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for creating a booking request.
 * It contains the necessary information to create a booking.
 */
@Data
public class BookingCreateRequest {
    @NotNull(message = "Check-in date is mandatory")
    private LocalDate checkInDate;
    @NotNull(message = "Check-out date is mandatory")
    private LocalDate checkOutDate;
    @Min(value = 1, message = "Number of adults must be at least 1")
    private int numOfAdults;
    private int numOfChildren;
    private User user;
    private Property property;
    private String couponCode;
}
