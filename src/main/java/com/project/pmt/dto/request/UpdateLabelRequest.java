package com.project.pmt.dto.request;

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
public class UpdateLabelRequest {

    @Size(max= 50, message= "Label name must be less than 50 characters")
    private String name;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be a valid hex code")
    private String color;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
}
