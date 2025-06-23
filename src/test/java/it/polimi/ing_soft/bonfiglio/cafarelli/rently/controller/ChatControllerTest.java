package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ChatMessageRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChatMessage;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.ChatService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatController chatController;

    private User sender;
    private User receiver;
    private ChatMessage chatMessage;
    private ChatMessageRequest chatMessageRequest;
    private List<ChatMessage> chatMessages;
    private List<User> usersWithMessages;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup users
        sender = new User();
        sender.setId(1L);
        sender.setUsername("sender");

        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("receiver");

        // Setup chat message
        chatMessage = new ChatMessage();
        chatMessage.setId(1L);
        chatMessage.setContent("Hello");
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setSendAt(LocalDateTime.now());

        // Setup chat message request
        chatMessageRequest = new ChatMessageRequest();
        chatMessageRequest.setContent("Hello");
        chatMessageRequest.setReceiverId(2L);

        // Setup chat messages list
        chatMessages = new ArrayList<>();
        chatMessages.add(chatMessage);

        // Setup users with messages list
        usersWithMessages = new ArrayList<>();
        usersWithMessages.add(receiver);
    }

    @Test
    void sendMessage_success() {
        // Arrange
        when(chatService.sendMessage(any(ChatMessageRequest.class))).thenReturn(chatMessage);

        // Act
        ResponseEntity<ChatMessage> response = chatController.sendMessage(chatMessageRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(chatMessage, response.getBody());
        verify(chatService).sendMessage(chatMessageRequest);
    }

    @Test
    void getConversation_success() {
        // Arrange
        when(chatService.getConversation(2L)).thenReturn(chatMessages);

        // Act
        ResponseEntity<List<ChatMessage>> response = chatController.getConversation(2L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(chatMessage, response.getBody().get(0));
        verify(chatService).getConversation(2L);
    }

    @Test
    void getUsersWithMessages_success() {
        // Arrange
        when(chatService.getUsersWithMessages()).thenReturn(usersWithMessages);

        // Act
        ResponseEntity<List<User>> response = chatController.getUsersWithMessages();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(receiver, response.getBody().get(0));
        verify(chatService).getUsersWithMessages();
    }
}