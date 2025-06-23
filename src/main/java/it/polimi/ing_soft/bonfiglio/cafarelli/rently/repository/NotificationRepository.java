package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Finds all notifications for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of notifications for the specified user
     */
    List<Notification> findByUserId(Long userId);

    /**
     * Finds a notification by its ID and the user's ID.
     *
     * @param notificationId the ID of the notification
     * @param userId the ID of the user
     * @return an Optional containing the notification if found, or empty if not found
     */
    Optional<Notification> findByIdAndUserId(Long notificationId, Long userId);
}
