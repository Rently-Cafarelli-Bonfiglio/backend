package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.ChatMessageBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChatMessage;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Implementation of the {@link ChatMessageBuilder} interface for building {@link ChatMessage} objects.
 */
// This class uses the Builder design pattern to create instances of ChatMessage.
@NoArgsConstructor
public class ChatMessageBuilderImplementation implements ChatMessageBuilder {
    private Long id;
    private String content;
    private User sender;
    private User receiver;
    private LocalDateTime sendAt;

    @Override
    public ChatMessageBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public ChatMessageBuilder content(String content) {
        this.content = content;
        return this;
    }

    @Override
    public ChatMessageBuilder sender(User sender) {
        this.sender = sender;
        return this;
    }

    @Override
    public ChatMessageBuilder receiver(User receiver) {
        this.receiver = receiver;
        return this;
    }

    @Override
    public ChatMessageBuilder sendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
        return this;
    }

    @Override
    public ChatMessage build() {

        return new ChatMessage(id, content, sender, receiver, sendAt);
    }
}