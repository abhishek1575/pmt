package com.project.pmt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {
    private Long id;

    private String name;

    private String description;

    private String key;

    private UserResponse lead;

    private String iconUrl;

    private Boolean archived;

    private Set<UserResponse> members;

    private Long issueCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}