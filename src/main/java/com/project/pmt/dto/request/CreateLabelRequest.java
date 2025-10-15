package com.project.pmt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLabelRequest {
    @NotBlank(message= "Label is required")
    @Size(max= 50, message= "Label name must be less than 50 characters")
    private String name;

    @NotBlank(message= "Color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be a valid hex code")
    private String color;

    @Size(max= 200, message= "Description must be less than 200 characters")
    private String description;
}
