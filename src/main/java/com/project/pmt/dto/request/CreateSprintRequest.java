package com.project.pmt.dto.request;

import com.project.pmt.enums.SprintState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSprintRequest {
    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotBlank(message = "Sprint name is required")
    @Size(max = 100, message = "Sprint name must not exceed 100 characters")
    private String name;

    @Size(max= 500, message = "Sprint goal must not exceed 500 characters")
    private String goal;

    private LocalDate startDate;

    private LocalDate endDate;

    private SprintState state = SprintState.PLANNED;
}
