package com.project.pmt.service;

import com.project.pmt.dto.response.NotificationResponse;
import com.project.pmt.entity.Issue;
import com.project.pmt.entity.Notification;
import com.project.pmt.entity.User;
import com.project.pmt.enums.NotificationType;
import com.project.pmt.exceptions.ResourceNotFoundException;
import com.project.pmt.mapper.NotificationMapper;
import com.project.pmt.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final NotificationMapper notificationMapper;

    @Transactional
    public NotificationResponse createNotification(
            User user,
            NotificationType type,
            String message,
            String link,
            Issue issue) {

        log.info("Creating notification for user: {}", user.getId());

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(message);
        notification.setLink(link);
        notification.setIssue(issue);
        notification.setRead(false);

        notification = notificationRepository.save(notification);
        log.info("Notification created successfully: {}", notification.getId());

        return notificationMapper.toResponse(notification);
    }

    public List<NotificationResponse> getMyNotifications() {
        User currentUser = userService.getCurrentUserEntity();
        return notificationRepository.findByUserId(currentUser.getId()).stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getUnreadNotifications() {
        User currentUser = userService.getCurrentUserEntity();
        return notificationRepository.findByUserIdAndReadFalse(currentUser.getId()).stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount() {
        User currentUser = userService.getCurrentUserEntity();
        return notificationRepository.countByUserIdAndReadFalse(currentUser.getId());
    }

    @Transactional
    public NotificationResponse markAsRead(Long id) {
        log.info("Marking notification as read: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));

        User currentUser = userService.getCurrentUserEntity();
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Notification", "id", id);
        }

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);
        log.info("Notification marked as read: {}", notification.getId());

        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public void markAllAsRead() {
        log.info("Marking all notifications as read");

        User currentUser = userService.getCurrentUserEntity();
        notificationRepository.markAllAsReadForUser(currentUser.getId());
        log.info("All notifications marked as read");
    }

    @Transactional
    public void deleteNotification(Long id) {
        log.info("Deleting notification: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));

        User currentUser = userService.getCurrentUserEntity();
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Notification", "id", id);
        }

        notificationRepository.delete(notification);
        log.info("Notification deleted successfully: {}", id);
    }

    @Transactional
    public void deleteAllNotifications() {
        log.info("Deleting all notifications");

        User currentUser = userService.getCurrentUserEntity();
        notificationRepository.deleteAllByUserId(currentUser.getId());
        log.info("All notifications deleted");
    }
}