package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation.NotificationBuilderImplementation;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Notification;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.NotificationRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.NotificationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the NotificationService interface.
 * This class provides methods for managing notifications in the system.
 */
@Service
@AllArgsConstructor
public class NotificationServiceImplementation implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Notification createNotification(@NonNull String username, @NonNull String message, @NonNull String type) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        Notification notification = new NotificationBuilderImplementation()
                .message(message)
                .type(type)
                .read(false)
                .user(user)
                .build();

        return notificationRepository.save(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Notification> getNotificationsByUserId(@NonNull Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class);
        }

        return notificationRepository.findByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Notification> getNotificationByIdAndUserId(@NonNull Long notificationId, @NonNull Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class);
        }

        return notificationRepository.findByIdAndUserId(notificationId, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CustomResponse markAsRead(@NonNull Long notificationId, @NonNull Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Notification.class));

        notification.setRead(true);
        notificationRepository.save(notification);

        return new CustomResponse("Notification marked as read successfully");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CustomResponse markAllAsRead(@NonNull Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class);
        }

        List<Notification> notifications = notificationRepository.findByUserId(userId);

        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        return new CustomResponse("All notifications marked as read successfully");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CustomResponse deleteNotification(@NonNull Long notificationId, @NonNull Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Notification.class));

        notificationRepository.delete(notification);

        return new CustomResponse("Notification deleted successfully");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CustomResponse deleteAllNotifications(@NonNull Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class);
        }

        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notificationRepository.deleteAll(notifications);

        return new CustomResponse("All notifications deleted successfully");
    }
}
