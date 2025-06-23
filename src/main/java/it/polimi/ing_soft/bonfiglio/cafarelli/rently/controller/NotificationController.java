package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Notification;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.NotificationService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing notification-related operations.
 * <p>
 * This controller provides endpoints for retrieving, marking as read, and deleting
 * notifications for users. It handles all notification management operations.
 * </p>
 */
@RestController
@RequestMapping(ApiPathUtil.REST_PATH + "/notifications")
@AllArgsConstructor
@Tag(name = "Notifications", description = "API for notification management operations")
public class NotificationController {

    /**
     * Service responsible for handling business logic related to notifications.
     */
    private final NotificationService notificationService;

    /**
     * Service responsible for handling business logic related to users.
     */
    private final UserService userService;

    /**
     * Retrieves all notifications for the current user.
     * <p>
     * This endpoint returns a list of all notifications for the authenticated user.
     * </p>
     *
     * @return ResponseEntity containing a list of notifications
     */
    @Operation(
        summary = "Get all notifications",
        description = "Retrieves all notifications for the current user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of notifications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Notification.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        )
    })
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        Long userId = userService.getCurrentUser().getId();
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Retrieves a specific notification by its ID.
     * <p>
     * This endpoint allows users to get detailed information about a specific notification.
     * </p>
     *
     * @param notificationId the ID of the notification to retrieve
     * @return ResponseEntity containing the notification details
     */
    @Operation(
        summary = "Get notification by ID",
        description = "Retrieves detailed information about a specific notification"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Notification.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notification not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        )
    })
    @GetMapping("/{notificationId}")
    public ResponseEntity<Notification> getNotificationById(
        @Parameter(description = "ID of the notification to retrieve", required = true)
        @PathVariable Long notificationId) {
        Long userId = userService.getCurrentUser().getId();
        return notificationService.getNotificationByIdAndUserId(notificationId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marks a notification as read.
     * <p>
     * This endpoint allows users to mark a specific notification as read.
     * </p>
     *
     * @param notificationId the ID of the notification to mark as read
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Mark notification as read",
        description = "Marks a specific notification as read"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification marked as read successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notification not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        )
    })
    @PostMapping("/{notificationId}/mark-read")
    public ResponseEntity<CustomResponse> markAsRead(
        @Parameter(description = "ID of the notification to mark as read", required = true)
        @PathVariable Long notificationId) {
        Long userId = userService.getCurrentUser().getId();
        CustomResponse response = notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Marks all notifications for the current user as read.
     * <p>
     * This endpoint allows users to mark all their notifications as read at once.
     * </p>
     *
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Mark all notifications as read",
        description = "Marks all notifications for the current user as read"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All notifications marked as read successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        )
    })
    @PostMapping("/mark-all-read")
    public ResponseEntity<CustomResponse> markAllAsRead() {
        Long userId = userService.getCurrentUser().getId();
        CustomResponse response = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a specific notification.
     * <p>
     * This endpoint allows users to delete a specific notification.
     * </p>
     *
     * @param notificationId the ID of the notification to delete
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Delete notification",
        description = "Deletes a specific notification"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification deleted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notification not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        )
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<CustomResponse> deleteNotification(
        @Parameter(description = "ID of the notification to delete", required = true)
        @PathVariable Long notificationId) {
        Long userId = userService.getCurrentUser().getId();
        CustomResponse response = notificationService.deleteNotification(notificationId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes all notifications for the current user.
     * <p>
     * This endpoint allows users to delete all their notifications at once.
     * </p>
     *
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Delete all notifications",
        description = "Deletes all notifications for the current user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All notifications deleted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        )
    })
    @DeleteMapping("/delete-all")
    public ResponseEntity<CustomResponse> deleteAllNotifications() {
        Long userId = userService.getCurrentUser().getId();
        CustomResponse response = notificationService.deleteAllNotifications(userId);
        return ResponseEntity.ok(response);
    }
}
