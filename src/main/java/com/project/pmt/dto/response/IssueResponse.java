package com.project.pmt.dto.response;

import com.project.pmt.enums.IssueStatus;
import com.project.pmt.enums.IssueType;
import com.project.pmt.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueResponse {

    private Long id;

    private String issueKey;

    private String title;

    private String description;

    private IssueType type;

    private Priority priority;

    private IssueStatus status;

    private UserResponse reporter;

    private UserResponse assignee;

    private SprintResponse sprint;

    private LocalDateTime dueDate;

    private Integer estimatedHours;

    private Integer loggedHours;

    private Integer boardOrder;

    private Set<LabelResponse> labels;

    private Long commentCount;

    private Long attachmentCount;

    private IssueResponse parentIssue;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;
}
