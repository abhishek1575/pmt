package com.project.pmt.mapper;

import com.project.pmt.dto.response.LabelResponse;
import com.project.pmt.entity.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {
    public LabelResponse toResponse(Label label) {
        if (label == null) {
            return null;
        }

        return LabelResponse.builder()
                .id(label.getId())
                .name(label.getName())
                .color(label.getColor())
                .description(label.getDescription())
                .createdAt(label.getCreatedAt())
                .updatedAt(label.getUpdatedAt())
                .build();
    }
}
