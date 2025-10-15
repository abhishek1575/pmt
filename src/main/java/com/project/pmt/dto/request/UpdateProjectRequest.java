package com.project.pmt.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProjectRequest {
    @Size(max=100, message = "Project name must be less than 100 characters")
    private String name;

    @Size(max=1000, message="description must be less than 1000 characters")
    private String description;

    private Long leadId;

    private String iconUrl;

    private Boolean archived;

    private Set<Long> memberIds;
}
