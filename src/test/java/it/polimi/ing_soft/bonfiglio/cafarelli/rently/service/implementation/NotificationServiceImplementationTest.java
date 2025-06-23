package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Notification;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.NotificationRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceImplementationTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private NotificationServiceImplementation notificationService;

    private User user;
    private Notification notification;
    private List<Notification> notifications;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        // Setup notification
        notification = new Notification();
        notification.setId(1L);
        notification.setMessage("Test notification");
        notification.setType("TEST");
        notification.setRead(false);
        notification.setUser(user);
        
        // Setup notification list
        notifications = new ArrayList<>();
        notifications.add(notification);
        
        // Setup second notification
        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setMessage("Another test notification");
        notification2.setType("TEST");
        notification2.setRead(false);
        notification2.setUser(user);
        notifications.add(notification2);
    }

    @Test
    void createNotification_success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification savedNotification = invocation.getArgument(0);
            savedNotification.setId(1L);
            return savedNotification;
        });
        
        // Act
        Notification result = notificationService.createNotification("testuser", "Test notification", "TEST");
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test notification", result.getMessage());
        assertEquals("TEST", result.getType());
        assertFalse(result.isRead());
        assertEquals(user, result.getUser());
        verify(notificationRepository).save(any(Notification.class));
    }
    
    @Test
    void createNotification_userNotFound_throwsException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            notificationService.createNotification("nonexistent", "Test notification", "TEST")
        );
        verify(notificationRepository, never()).save(any(Notification.class));
    }
    
    @Test
    void getNotificationsByUserId_success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);
        
        // Act
        List<Notification> result = notificationService.getNotificationsByUserId(1L);
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(notification, result.get(0));
    }
    
    @Test
    void getNotificationsByUserId_userNotFound_throwsException() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            notificationService.getNotificationsByUserId(999L)
        );
    }
    
    @Test
    void getNotificationByIdAndUserId_success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(notification));
        
        // Act
        Optional<Notification> result = notificationService.getNotificationByIdAndUserId(1L, 1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(notification, result.get());
    }
    
    @Test
    void getNotificationByIdAndUserId_userNotFound_throwsException() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            notificationService.getNotificationByIdAndUserId(1L, 999L)
        );
    }
    
    @Test
    void getNotificationByIdAndUserId_notificationNotFound_returnsEmptyOptional() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());
        
        // Act
        Optional<Notification> result = notificationService.getNotificationByIdAndUserId(999L, 1L);
        
        // Assert
        assertTrue(result.isEmpty());
    }
    
    @Test
    void markAsRead_success() {
        // Arrange
        when(notificationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(notification));
        
        // Act
        CustomResponse response = notificationService.markAsRead(1L, 1L);
        
        // Assert
        assertEquals("Notification marked as read successfully", response.getMessage());
        assertTrue(notification.isRead());
        verify(notificationRepository).save(notification);
    }
    
    @Test
    void markAsRead_notificationNotFound_throwsException() {
        // Arrange
        when(notificationRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            notificationService.markAsRead(999L, 1L)
        );
    }
    
    @Test
    void markAllAsRead_success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);
        
        // Act
        CustomResponse response = notificationService.markAllAsRead(1L);
        
        // Assert
        assertEquals("All notifications marked as read successfully", response.getMessage());
        assertTrue(notifications.get(0).isRead());
        assertTrue(notifications.get(1).isRead());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }
    
    @Test
    void markAllAsRead_userNotFound_throwsException() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            notificationService.markAllAsRead(999L)
        );
    }
    
    @Test
    void deleteNotification_success() {
        // Arrange
        when(notificationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(notification));
        
        // Act
        CustomResponse response = notificationService.deleteNotification(1L, 1L);
        
        // Assert
        assertEquals("Notification deleted successfully", response.getMessage());
        verify(notificationRepository).delete(notification);
    }
    
    @Test
    void deleteNotification_notificationNotFound_throwsException() {
        // Arrange
        when(notificationRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            notificationService.deleteNotification(999L, 1L)
        );
    }
    
    @Test
    void deleteAllNotifications_success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);
        
        // Act
        CustomResponse response = notificationService.deleteAllNotifications(1L);
        
        // Assert
        assertEquals("All notifications deleted successfully", response.getMessage());
        verify(notificationRepository).deleteAll(notifications);
    }
    
    @Test
    void deleteAllNotifications_userNotFound_throwsException() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            notificationService.deleteAllNotifications(999L)
        );
    }
}