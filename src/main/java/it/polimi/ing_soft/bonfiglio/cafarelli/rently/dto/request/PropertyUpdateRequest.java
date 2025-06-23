package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for updating a property.
 * It contains the necessary information to update a property listing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyUpdateRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @Positive(message = "Price must be positive")
    private BigDecimal pricePerNight;

    @Min(value = 1, message = "At least one bedroom is required")
    private Integer bedrooms;

    @Min(value = 1, message = "At least one bathroom is required")
    private Integer bathrooms;

    @Min(value = 1, message = "Maximum number of guests must be at least 1")
    private Integer maxGuests;

    private Boolean isAvailable;
}