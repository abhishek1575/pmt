package com.project.pmt.dto.response;

import com.project.pmt.enums.SprintState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprintResponse {
    private Long id;

    private String name;

    private String goal;

    private LocalDate startDate;

    private LocalDate endDate;

    private SprintState state;

    private Long projectId;

    private Long issueCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
