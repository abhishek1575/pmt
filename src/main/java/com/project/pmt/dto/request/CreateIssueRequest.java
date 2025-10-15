package com.project.pmt.dto.request;

import com.project.pmt.enums.IssueStatus;
import com.project.pmt.enums.IssueType;
import com.project.pmt.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateIssueRequest {
    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotBlank(message= "Title is required")
    @Size(max= 200, message= "Title can have at most 200 characters")
    private String title;

    private String description;

    @NotNull(message = "Issue type is required")
    private IssueType issueType;

    @NotNull(message = "Priority is required")
    private Priority priority;

    private IssueStatus status = IssueStatus.TODO;

    private Long assigneeId;

    private Long sprintId;

    private LocalDateTime dueDate;

    private Integer estimateHours;

    private Set<Long> labelIds;

    private Long parentIssueId;
}
