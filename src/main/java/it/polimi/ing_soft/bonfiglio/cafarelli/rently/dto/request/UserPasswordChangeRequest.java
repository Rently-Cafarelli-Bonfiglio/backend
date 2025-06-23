package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for changing user password.
 * It contains the current password, new password, and a confirmation of the new password.
 */
@Data
@AllArgsConstructor
public class UserPasswordChangeRequest {

    @NotBlank(message = "Current Password is mandatory")
    private final String currentPassword;

    @NotBlank(message = "New Password is mandatory")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long, contain one uppercase letter, one special character, and one number"
    )
    private String newPassword;

    @NotBlank(message = "Repeat password is mandatory")
    private String repeatNewPassword;
}
