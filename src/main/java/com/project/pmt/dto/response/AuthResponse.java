package com.project.pmt.dto.response;

import com.project.pmt.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;

    private String refreshToken;

    private String type = "Bearer";

    private Long id;

    private String username;

    private String email;

    private String fullName;

    private Role role;

}
