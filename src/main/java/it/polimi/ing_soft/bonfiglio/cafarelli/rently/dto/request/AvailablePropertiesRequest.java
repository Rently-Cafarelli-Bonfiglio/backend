package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for requesting available properties based on check-in and check-out dates, city, and number of guests.
 */

@Data
@AllArgsConstructor
public class AvailablePropertiesRequest {
    @Future(message = "Check-in date cannot be in the past")
    LocalDate checkInDate;
    @Future(message = "Check-out date cannot be in the past")
    LocalDate checkOutDate;
    @NotBlank(message = "Please select a city")
    String city;
    @NotNull(message = "Please enter the number of adults") @Min(value = 1, message = "Please enter at least one adult")
    int numOfAdults;
    @Min(value = 0, message = "Number of children must not be less than zero")
    int numOfChildren;
}