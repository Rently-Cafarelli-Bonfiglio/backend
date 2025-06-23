package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for creating a property.
 * It contains the necessary information to create a property listing.
 */
@Data
public class PropertyCreateRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "State is mandatory")
    private String state;

    @NotBlank(message = "Country is mandatory")
    private String country;

    @NotNull(message = "Price per night is mandatory")
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerNight;

    @Min(value = 1, message = "At least one bedroom is required")
    private int bedrooms;

    @Min(value = 1, message = "At least one bathroom is required")
    private int bathrooms;

    @Min(value = 1, message = "Maximum number of guests must be at least 1")
    private int maxGuests;
}