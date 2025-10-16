package com.project.pmt.mapper;

import com.project.pmt.dto.response.IssueResponse;
import com.project.pmt.dto.response.PageResponse;
import com.project.pmt.entity.Issue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IssueMapper {
    private final UserMapper userMapper;
    private final SprintMapper sprintMapper;
    private final LabelMapper labelMapper;


    public IssueResponse toResponse(Issue issue) {
        if (issue == null) {
            return null;
        }

        return IssueResponse.builder()
                .id(issue.getId())
                .issueKey(issue.getIssueKey())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .type(issue.getType())
                .priority(issue.getPriority())
                .status(issue.getStatus())
                .reporter(userMapper.toResponse(issue.getReporter()))
                .assignee(userMapper.toResponse(issue.getAssignee()))
                .sprint(sprintMapper.toResponse(issue.getSprint()))
                .dueDate(issue.getDueDate())
                .estimatedHours(issue.getEstimatedHours())
                .loggedHours(issue.getLoggedHours())
                .boardOrder(issue.getBoardOrder())
                .labels(issue.getLabels().stream()
                        .map(labelMapper::toResponse)
                        .collect(Collectors.toSet()))
                .commentCount((long) issue.getComments().size())
                .attachmentCount((long) issue.getAttachments().size())
                .parentIssue(toSimpleResponse(issue.getParentIssue()))
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .resolvedAt(issue.getResolvedAt())
                .build();
    }

    private IssueResponse toSimpleResponse(Issue issue) {
        if (issue == null) {
            return null;
        }

        return IssueResponse.builder()
                .id(issue.getId())
                .issueKey(issue.getIssueKey())
                .title(issue.getTitle())
                .type(issue.getType())
                .status(issue.getStatus())
                .build();
    }

    public PageResponse<IssueResponse> toPageResponse(Page<Issue> page) {
        return PageResponse.<IssueResponse>builder()
                .content(page.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();
    }
}


