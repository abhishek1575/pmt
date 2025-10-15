package com.project.pmt.dto.response;

import com.project.pmt.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;

    private NotificationType type;

    private String message;

    private String link;

    private Boolean read;

    private LocalDateTime readAt;

    private Long issueId;

    private LocalDateTime createdAt;
}
