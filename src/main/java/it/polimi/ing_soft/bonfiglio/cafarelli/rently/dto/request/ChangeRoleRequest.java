package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data

/**
 * Request DTO for changing a user's role.
 * Contains the motivation for the role change.
 */

public class ChangeRoleRequest {

    @NotBlank(message = "motivation cannot be blank")
    private String motivation;
}
