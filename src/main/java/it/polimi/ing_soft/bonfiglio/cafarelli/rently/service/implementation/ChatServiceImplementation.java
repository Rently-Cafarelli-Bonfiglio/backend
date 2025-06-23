package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;


import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.ChatMessageBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ChatMessageRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChatMessage;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.ChatMessageRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.ChatService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class implements the ChatService interface, providing methods for sending messages and retrieving conversations.
 * It uses a ChatMessageRepository to interact with the database and a SimpMessagingTemplate to send messages via WebSocket.
 */
@Service
@AllArgsConstructor
public class ChatServiceImplementation implements ChatService {

    private final ChatMessageRepository chatMessageRepository;

    private final ChatMessageBuilder chatMessageBuilder;

    private final UserService userService;

    /**
     * Sends a chat message from the authenticated user to another user.
     * 
     * @param chatMessageRequest the request containing message content and receiver information
     * @return the created and saved chat message entity
     */
    @Transactional
    public ChatMessage sendMessage(ChatMessageRequest chatMessageRequest) {
        User sender = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User receiver = userService.findById(chatMessageRequest.getReceiverId());

        ChatMessage chatMessage = chatMessageBuilder
                .content(chatMessageRequest.getContent())
                .sender(sender)
                .receiver(receiver)
                .sendAt(LocalDateTime.now())
                .build();

        // Save the message to the database
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * Retrieves the conversation between the authenticated user and another user.
     * 
     * @param receiverId the ID of the user to retrieve conversation with
     * @return a list of chat messages representing the conversation
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getConversation(Long receiverId) {
        User sender = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Retrieve the recipient user from the database using the ID received in the request
        User receiver = userService.findById(receiverId);

        return chatMessageRepository.findConversationBetweenUsers(sender, receiver);
    }

    /**
     * Retrieves all users that have exchanged messages with the authenticated user.
     * 
     * @return a list of users that have exchanged messages with the authenticated user
     */
    @Transactional(readOnly = true)
    public List<User> getUsersWithMessages() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Retrieve all distinct messages where the user is the recipient
        List<ChatMessage> messages = chatMessageRepository.findDistinctByReceiver(user);

        // Extract unique users from the messages
        return messages.stream()
                .map(message -> message.getSender().equals(user) ? message.getReceiver() : message.getSender())
                .distinct()
                .toList();
    }
}
