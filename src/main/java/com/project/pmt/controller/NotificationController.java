package com.project.pmt.controller;

import com.project.pmt.dto.response.ApiResponse;
import com.project.pmt.dto.response.NotificationResponse;
import com.project.pmt.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Notification Controller
 * Handles user notifications for assignments, mentions, status changes, etc.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Notifications", description = "User notification management endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get all notifications for current user
     * GET /api/notifications
     */
    @GetMapping
    @Operation(summary = "Get my notifications", description = "Get all notifications for current user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        List<NotificationResponse> response = notificationService.getMyNotifications();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get unread notifications only
     * GET /api/notifications/unread
     */
    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Get all unread notifications for current user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications() {
        List<NotificationResponse> response = notificationService.getUnreadNotifications();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get unread notification count
     * GET /api/notifications/unread/count
     */
    @GetMapping("/unread/count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        Long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * Mark notification as read
     * PATCH /api/notifications/{id}/read
     */
    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark a specific notification as read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }

    /**
     * Mark all notifications as read
     * PATCH /api/notifications/read-all
     */
    @PatchMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for current user")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }

    /**
     * Delete notification
     * DELETE /api/notifications/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Delete a specific notification")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", null));
    }

    /**
     * Delete all notifications
     * DELETE /api/notifications/all
     */
    @DeleteMapping("/all")
    @Operation(summary = "Delete all notifications", description = "Delete all notifications for current user")
    public ResponseEntity<ApiResponse<Void>> deleteAllNotifications() {
        notificationService.deleteAllNotifications();
        return ResponseEntity.ok(ApiResponse.success("All notifications deleted successfully", null));
    }
}