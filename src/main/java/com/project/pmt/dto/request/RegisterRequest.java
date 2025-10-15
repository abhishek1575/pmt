package com.project.pmt.dto.request;

import com.project.pmt.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterRequest {
    @NotBlank(message= "Username is required")
    @Size(min= 3, max= 50, message= "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message= "Password is required")
    @Size(min= 6, max= 100, message= "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message= "Email is required")
    @Email(message= "Email should be valid")
    @Size(max= 100)
    private String email;

    @NotBlank(message= "Full name is required")
    @Size(min= 3, max= 100, message= "Full name must be between 3 and 100 characters")
    private String fullName;

    private String phoneNumber;
    private String department;
    private Role role = Role.USER;
}
