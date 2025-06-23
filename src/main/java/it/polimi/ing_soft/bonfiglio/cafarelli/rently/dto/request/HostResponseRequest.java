package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for host response to a review
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostResponseRequest {
    
    /**
     * The response text from the host
     */
    @NotBlank(message = "Response cannot be blank")
    @Size(min = 1, max = 1000, message = "Response must be between 1 and 1000 characters")
    private String response;
}