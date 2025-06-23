package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChatMessage;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;

import java.time.LocalDateTime;

/**
 * Interface for building {@link ChatMessage} objects.
 */
public interface ChatMessageBuilder {
    ChatMessageBuilder id(Long id);

    ChatMessageBuilder content(String content);

    ChatMessageBuilder sender(User sender);

    ChatMessageBuilder receiver(User receiver);

    ChatMessageBuilder sendAt(LocalDateTime sendAt);

    ChatMessage build();

}
