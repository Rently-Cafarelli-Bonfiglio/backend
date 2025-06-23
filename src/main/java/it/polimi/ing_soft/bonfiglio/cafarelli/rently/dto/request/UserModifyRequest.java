package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for modifying user information.
 * It contains the necessary information to modify a user's details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserModifyRequest {

        @NotBlank(message = "Username is mandatory")
        private String username;

        @NotBlank(message = "Email is mandatory")
        private String email;

        @NotBlank(message = "Password is mandatory")
        private String password;
}
