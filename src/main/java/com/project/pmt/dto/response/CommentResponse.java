package com.project.pmt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

    private Long id;

    private String body;

    private UserResponse user;

    private Long issueId;

    private Boolean edited;

    private LocalDateTime editedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
