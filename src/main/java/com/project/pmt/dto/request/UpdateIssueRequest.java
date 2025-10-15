package com.project.pmt.dto.request;

import com.project.pmt.enums.IssueStatus;
import com.project.pmt.enums.IssueType;
import com.project.pmt.enums.Priority;
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
public class UpdateIssueRequest {
    @Size(max=200, message = "Title can have maximum 200 characters")
    private String title;

    private String description;

    private IssueType type;

    private Priority priority;

    private IssueStatus status;

    private Long assigneeId;

    private Long sprintId;

    private LocalDateTime dueDate;

    private Integer estimatedHours;

    private Integer loggedHours;

    private Set<Long> labelIds;

    private Integer boardOrder;

}
