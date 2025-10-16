package com.project.pmt.mapper;

import com.project.pmt.dto.response.NotificationResponse;
import com.project.pmt.entity.Notification;

public class NotificationMapper {
    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .link(notification.getLink())
                .read(notification.getRead())
                .readAt(notification.getReadAt())
                .issueId(notification.getIssue() != null ? notification.getIssue().getId() : null)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
