package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.ChatMessageBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ChatMessageRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChatMessage;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.ChatMessageRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatServiceImplementationTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatMessageBuilder chatMessageBuilder;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ChatServiceImplementation chatService;

    private User sender;
    private User receiver;
    private ChatMessage chatMessage;
    private ChatMessageRequest chatMessageRequest;
    private List<ChatMessage> chatMessages;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup security context mock
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

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

        // Setup chat message builder
        when(chatMessageBuilder.content(anyString())).thenReturn(chatMessageBuilder);
        when(chatMessageBuilder.sender(any(User.class))).thenReturn(chatMessageBuilder);
        when(chatMessageBuilder.receiver(any(User.class))).thenReturn(chatMessageBuilder);
        when(chatMessageBuilder.sendAt(any(LocalDateTime.class))).thenReturn(chatMessageBuilder);
        when(chatMessageBuilder.build()).thenReturn(chatMessage);
    }

    @Test
    void sendMessage_success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(sender);
        when(userService.findById(2L)).thenReturn(receiver);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // Act
        ChatMessage result = chatService.sendMessage(chatMessageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Hello", result.getContent());
        assertEquals(sender, result.getSender());
        assertEquals(receiver, result.getReceiver());
        verify(chatMessageRepository).save(chatMessage);
    }

    @Test
    void getConversation_success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(sender);
        when(userService.findById(2L)).thenReturn(receiver);
        when(chatMessageRepository.findConversationBetweenUsers(sender, receiver)).thenReturn(chatMessages);

        // Act
        List<ChatMessage> result = chatService.getConversation(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(chatMessage, result.get(0));
        verify(chatMessageRepository).findConversationBetweenUsers(sender, receiver);
    }

    @Test
    void getUsersWithMessages_success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(sender);
        when(chatMessageRepository.findDistinctByReceiver(sender)).thenReturn(chatMessages);

        // Act
        List<User> result = chatService.getUsersWithMessages();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(receiver, result.get(0));
        verify(chatMessageRepository).findDistinctByReceiver(sender);
    }

    @Test
    void getUsersWithMessages_withSenderAsCurrentUser() {
        // Arrange
        ChatMessage messageSentByCurrentUser = new ChatMessage();
        messageSentByCurrentUser.setSender(sender);
        messageSentByCurrentUser.setReceiver(receiver);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(messageSentByCurrentUser);

        when(authentication.getPrincipal()).thenReturn(sender);
        when(chatMessageRepository.findDistinctByReceiver(sender)).thenReturn(messages);

        // Act
        List<User> result = chatService.getUsersWithMessages();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(receiver, result.get(0));
        verify(chatMessageRepository).findDistinctByReceiver(sender);
    }

    @Test
    void getUsersWithMessages_withReceiverAsCurrentUser() {
        // Arrange
        ChatMessage messageReceivedByCurrentUser = new ChatMessage();
        messageReceivedByCurrentUser.setSender(receiver);
        messageReceivedByCurrentUser.setReceiver(sender);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(messageReceivedByCurrentUser);

        when(authentication.getPrincipal()).thenReturn(sender);
        when(chatMessageRepository.findDistinctByReceiver(sender)).thenReturn(messages);

        // Act
        List<User> result = chatService.getUsersWithMessages();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(receiver, result.get(0));
        verify(chatMessageRepository).findDistinctByReceiver(sender);
    }
}