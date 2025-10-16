package com.project.pmt.mapper;

import com.project.pmt.dto.response.SprintResponse;
import com.project.pmt.entity.Sprint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SprintMapper {
    public SprintResponse toResponse(Sprint sprint){
        if (sprint == null){
            return null;
        }

        return SprintResponse.builder()
                .id(sprint.getId())
                .name(sprint.getName())
                .goal(sprint.getGoal())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .state(sprint.getState())
                .projectId(sprint.getProject().getId())
                .issueCount((long) sprint.getIssues().size())
                .createdAt(sprint.getCreatedAt())
                .updatedAt(sprint.getUpdatedAt())
                .build();
    }
}
