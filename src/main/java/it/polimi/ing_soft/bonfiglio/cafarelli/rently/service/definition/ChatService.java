package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ChatMessageRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChatMessage;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;

import java.util.List;

/**
 * This interface defines the contract for chat services.
 * It includes methods for sending messages and retrieving conversations.
 */
public interface ChatService {

    /**
     * Sends a chat message.
     *
     * @param chatMessageRequest the request containing message content and receiver information
     * @return the created chat message entity
     */
    public ChatMessage sendMessage(ChatMessageRequest chatMessageRequest);

    /**
     * Retrieves the conversation between the authenticated user and another user.
     *
     * @param receiverId the ID of the user to retrieve conversation with
     * @return a list of chat messages representing the conversation
     */
    public List<ChatMessage> getConversation(Long receiverId);

    /**
     * Retrieves all users that have exchanged messages with the authenticated user.
     *
     * @return a list of users that have exchanged messages with the authenticated user
     */
    public List<User> getUsersWithMessages();
}
