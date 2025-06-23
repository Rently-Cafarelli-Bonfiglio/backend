package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ChatMessageRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChatMessage;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.ChatService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling real-time chat operations via WebSocket.
 * <p>
 * This controller manages message exchanges between users through STOMP
 * WebSocket protocol. It handles sending and retrieving chat messages.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPathUtil.REST_PATH + "/chat")
public class ChatController {
    /**
     * Service responsible for chat message operations like sending and retrieving conversations.
     */
    private final ChatService chatService;

    /**
     * Service responsible for user-related operations like finding users by ID or username.
     */
    private final UserService userService;

    /**
     * Handles incoming chat messages and sends them to the appropriate recipient.
     * <p>
     * This method extracts the authenticated user from security context,
     * builds a chat message with sender, receiver, content and timestamp,
     * and processes it through the chat service.
     * </p>
     *
     * @param request the chat message request containing message content and receiver ID
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/send")
    @PreAuthorize("hasAuthority('ROLE_CLIENT') or hasAuthority('ROLE_HOST')")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody @Valid ChatMessageRequest request) {
        ChatMessage message = chatService.sendMessage(request);

        return ResponseEntity.ok(message);
    }

    /**
     * Retrieves conversation history between the authenticated user and another user.
     * <p>
     * This method extracts the authenticated user from security context
     * to prepare for conversation retrieval.
     * </p>
     *
     * @param userId the ID of the user to retrieve conversation with
     * @return a ResponseEntity containing the list of chat messages
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatMessage>> getConversation(@PathVariable Long userId) {
        List<ChatMessage> messages = chatService.getConversation(userId);

        return ResponseEntity.ok(messages);
    }

    /**
     * Retrieves all users that have exchanged messages with the authenticated host.
     * <p>
     * This endpoint is restricted to users with the HOST role.
     * </p>
     *
     * @return a ResponseEntity containing the list of users who have exchanged messages with the host
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<List<User>> getUsersWithMessages() {
        List<User> users = chatService.getUsersWithMessages();

        return ResponseEntity.ok(users);
    }
}
