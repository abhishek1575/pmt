package com.project.pmt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCommentRequest {

    @NotBlank(message = "Comment body is required")
    @Size(max= 5000, message = "Comment body must not exceed 5000 characters")
    private String body;

}
