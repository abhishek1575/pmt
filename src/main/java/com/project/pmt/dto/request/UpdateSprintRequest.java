package com.project.pmt.dto.request;

import com.project.pmt.enums.SprintState;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSprintRequest {
    @Size(max= 100, message= "Sprint name must be at most 100 characters")
    private String name;

    @Size(max= 500, message= "Sprint goal must be at most 500 characters")
    private String goal;

    private LocalDate startDate;

    private LocalDate endDate;

    private SprintState state;
}
