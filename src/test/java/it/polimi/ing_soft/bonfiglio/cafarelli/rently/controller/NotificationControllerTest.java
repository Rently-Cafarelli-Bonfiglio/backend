package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Notification;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.NotificationService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        when(userService.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    void getAllNotifications_ReturnsNotificationsList() {
        List<Notification> notifications = List.of(new Notification(), new Notification());
        when(notificationService.getNotificationsByUserId(1L)).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = notificationController.getAllNotifications();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getNotificationById_Found_ReturnsNotification() {
        Notification notification = new Notification();
        when(notificationService.getNotificationByIdAndUserId(10L, 1L)).thenReturn(Optional.of(notification));

        ResponseEntity<Notification> response = notificationController.getNotificationById(10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(notification, response.getBody());
    }

    @Test
    void getNotificationById_NotFound_Returns404() {
        when(notificationService.getNotificationByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        ResponseEntity<Notification> response = notificationController.getNotificationById(10L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void markAsRead_ReturnsSuccessResponse() {
        CustomResponse customResponse = new CustomResponse("Notification marked as read");
        when(notificationService.markAsRead(5L, 1L)).thenReturn(customResponse);

        ResponseEntity<CustomResponse> response = notificationController.markAsRead(5L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Notification marked as read", response.getBody().getMessage());
    }

    @Test
    void markAllAsRead_ReturnsSuccessResponse() {
        CustomResponse customResponse = new CustomResponse("All notifications marked as read");
        when(notificationService.markAllAsRead(1L)).thenReturn(customResponse);

        ResponseEntity<CustomResponse> response = notificationController.markAllAsRead();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("All notifications marked as read", response.getBody().getMessage());
    }

    @Test
    void deleteNotification_ReturnsSuccessResponse() {
        CustomResponse customResponse = new CustomResponse("Notification deleted");
        when(notificationService.deleteNotification(6L, 1L)).thenReturn(customResponse);

        ResponseEntity<CustomResponse> response = notificationController.deleteNotification(6L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Notification deleted", response.getBody().getMessage());
    }

    @Test
    void deleteAllNotifications_ReturnsSuccessResponse() {
        CustomResponse customResponse = new CustomResponse("All notifications deleted");
        when(notificationService.deleteAllNotifications(1L)).thenReturn(customResponse);

        ResponseEntity<CustomResponse> response = notificationController.deleteAllNotifications();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("All notifications deleted", response.getBody().getMessage());
    }
}
