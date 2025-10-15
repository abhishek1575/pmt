package com.project.pmt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommentRequest {
    @NotBlank(message = "Comment body is required")
    @Size(max=5000 , message = "Comment body must not exceed 5000 characters")
    private String body;
}
