package com.project.pmt.mapper;

import com.project.pmt.dto.response.PageResponse;
import com.project.pmt.dto.response.ProjectResponse;
import com.project.pmt.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectMapper {
    private final UserMapper userMapper;

    public ProjectResponse toResponse(Project project){
        if(project == null){
            return null;
        }
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .key(project.getKey())
                .description(project.getDescription())
                .lead(userMapper.toResponse(project.getLead()))
                .iconUrl(project.getIconUrl())
                .archived(project.getArchived())
                .members(project.getMembers().stream()
                        .map(userMapper::toResponse)
                        .collect(Collectors.toSet()))
                .issueCount((long) project.getIssues().size())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }


    public PageResponse<ProjectResponse> toPageResponse(Page<Project> page) {
        return PageResponse.<ProjectResponse>builder()
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
