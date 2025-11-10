package com.project.pmt.controller;

import com.project.pmt.dto.response.ApiResponse;
import com.project.pmt.dto.response.UserResponse;
import com.project.pmt.enums.Role;
import com.project.pmt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Controller
 * Handles administrative operations (user management, system configuration)
 * Only accessible by ADMIN and SUPER_ADMIN roles
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin", description = "Administrative management endpoints")
public class AdminController {

    private final UserService userService;

    /**
     * Get all users (Admin view)
     * GET /api/admin/users
     */
    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Get complete list of all users (Admin only)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get users by role
     * GET /api/admin/users/role/{role}
     */
    @GetMapping("/users/role/{role}")
    @Operation(summary = "Get users by role", description = "Get all users with specific role (Admin only)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable Role role) {
        List<UserResponse> response = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update user role
     * PATCH /api/admin/users/{id}/role?role=ADMIN
     */
    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update user role", description = "Change user role (Super Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        UserResponse response = userService.updateUserRole(id, role);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", response));
    }

    /**
     * Deactivate user account
     * PATCH /api/admin/users/{id}/deactivate
     */
    @PatchMapping("/users/{id}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate a user account (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
    }

    /**
     * Activate user account
     * PATCH /api/admin/users/{id}/activate
     */
    @PatchMapping("/users/{id}/activate")
    @Operation(summary = "Activate user", description = "Activate a user account (Admin only)")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", null));
    }

    /**
     * Delete user permanently
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete user", description = "Permanently delete a user account (Super Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    /**
     * Get system statistics (optional)
     * GET /api/admin/stats
     */
    @GetMapping("/stats")
    @Operation(summary = "Get system statistics", description = "Get overall system statistics")
    public ResponseEntity<ApiResponse<SystemStats>> getSystemStats() {
        // Implementation would require creating SystemStats class and service method
        // Placeholder for future implementation
        SystemStats stats = SystemStats.builder()
                .totalUsers(0L)
                .totalProjects(0L)
                .totalIssues(0L)
                .activeUsers(0L)
                .build();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Inner class for system statistics
     */
    @lombok.Data
    @lombok.Builder
    private static class SystemStats {
        private Long totalUsers;
        private Long totalProjects;
        private Long totalIssues;
        private Long activeUsers;
    }
}