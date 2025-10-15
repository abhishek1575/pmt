package com.project.pmt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentResponse {
    private Long id;

    private String filename;

    private String url;

    private String contentType;

    private Long size;

    private UserResponse uploadedBy;

    private Long issueId;

    private LocalDateTime createdAt;
}
