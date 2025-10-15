package com.project.pmt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelResponse {
    private Long id;

    private String name;

    private String color;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
