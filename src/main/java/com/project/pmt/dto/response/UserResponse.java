package com.project.pmt.dto.response;

import com.project.pmt.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;

    private String username;

    private String email;

    private String fullName;

    private Role role;

    private Boolean active;

    private String avatarUrl;

    private String phoneNumber;

    private String department;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
