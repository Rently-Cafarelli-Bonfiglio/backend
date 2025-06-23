package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

/**
 * Request DTO for adding a reply to a ticket.
 * Contains the content of the reply.
 */

public class AddTicketReplyRequest {
    @NotBlank(message = "Il contenuto della risposta è obbligatorio")
    private String content;
}