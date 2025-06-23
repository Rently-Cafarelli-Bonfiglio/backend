package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for user login request.
 * It contains the necessary information to log in a user.
 */
@Data
public class UserLoginRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
