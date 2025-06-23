package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Notification;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * This interface defines the contract for notification services.
 * It includes methods for creating, retrieving, marking as read, and deleting notifications.
 */
public interface NotificationService {

    /**
     * Creates a new notification for a specific user.
     *
     * @param username the username of the user to receive the notification
     * @param message the message content of the notification
     * @param type the type of notification
     * @return the created Notification object
     */
    Notification createNotification(@NonNull String username, @NonNull String message, @NonNull String type);

    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of notifications for the specified user
     */
    List<Notification> getNotificationsByUserId(@NonNull Long userId);

    /**
     * Retrieves a specific notification by its ID and user ID.
     *
     * @param notificationId the ID of the notification
     * @param userId the ID of the user
     * @return an Optional containing the notification if found, or empty if not found
     */
    Optional<Notification> getNotificationByIdAndUserId(@NonNull Long notificationId, @NonNull Long userId);

    /**
     * Marks a notification as read.
     *
     * @param notificationId the ID of the notification to mark as read
     * @param userId the ID of the user who owns the notification
     * @return a CustomResponse indicating the result of the operation
     */
    CustomResponse markAsRead(@NonNull Long notificationId, @NonNull Long userId);

    /**
     * Marks all notifications for a user as read.
     *
     * @param userId the ID of the user
     * @return a CustomResponse indicating the result of the operation
     */
    CustomResponse markAllAsRead(@NonNull Long userId);

    /**
     * Deletes a specific notification.
     *
     * @param notificationId the ID of the notification to delete
     * @param userId the ID of the user who owns the notification
     * @return a CustomResponse indicating the result of the operation
     */
    CustomResponse deleteNotification(@NonNull Long notificationId, @NonNull Long userId);

    /**
     * Deletes all notifications for a specific user.
     *
     * @param userId the ID of the user
     * @return a CustomResponse indicating the result of the operation
     */
    CustomResponse deleteAllNotifications(@NonNull Long userId);
}