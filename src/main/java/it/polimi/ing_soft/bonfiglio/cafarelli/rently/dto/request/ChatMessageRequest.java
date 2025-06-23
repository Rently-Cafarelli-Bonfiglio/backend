package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending a chat message.
 * It contains the content of the message and the ID of the receiver.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
    @NotBlank(message = "Message content cannot be empty")
    private String content;

    @NotNull(message = "Receiver ID is mandatory")
    private Long receiverId;
}