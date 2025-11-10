package com.project.pmt.controller;

import com.project.pmt.dto.request.CreateSprintRequest;
import com.project.pmt.dto.request.UpdateSprintRequest;
import com.project.pmt.dto.response.ApiResponse;
import com.project.pmt.dto.response.SprintResponse;
import com.project.pmt.service.SprintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Sprint Controller
 * Handles sprint/iteration management for agile workflows
 */
@RestController
@RequestMapping("/sprints")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Sprints", description = "Sprint/Iteration management endpoints")
public class SprintController {

    private final SprintService sprintService;

    /**
     * Create a new sprint
     * POST /api/sprints
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create sprint", description = "Create a new sprint for a project (Admin only)")
    public ResponseEntity<ApiResponse<SprintResponse>> createSprint(
            @Valid @RequestBody CreateSprintRequest request) {
        SprintResponse response = sprintService.createSprint(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sprint created successfully", response));
    }

    /**
     * Get sprint by ID
     * GET /api/sprints/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get sprint by ID", description = "Get sprint details by ID")
    public ResponseEntity<ApiResponse<SprintResponse>> getSprintById(@PathVariable Long id) {
        SprintResponse response = sprintService.getSprintById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all sprints for a project
     * GET /api/sprints/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get sprints by project", description = "Get all sprints for a specific project")
    public ResponseEntity<ApiResponse<List<SprintResponse>>> getSprintsByProject(
            @PathVariable Long projectId) {
        List<SprintResponse> response = sprintService.getSprintsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get active sprint for a project
     * GET /api/sprints/project/{projectId}/active
     */
    @GetMapping("/project/{projectId}/active")
    @Operation(summary = "Get active sprint", description = "Get currently active sprint for a project")
    public ResponseEntity<ApiResponse<SprintResponse>> getActiveSprint(@PathVariable Long projectId) {
        SprintResponse response = sprintService.getActiveSprint(projectId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update sprint
     * PUT /api/sprints/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update sprint", description = "Update sprint details (Admin only)")
    public ResponseEntity<ApiResponse<SprintResponse>> updateSprint(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSprintRequest request) {
        SprintResponse response = sprintService.updateSprint(id, request);
        return ResponseEntity.ok(ApiResponse.success("Sprint updated successfully", response));
    }

    /**
     * Start sprint
     * PATCH /api/sprints/{id}/start
     */
    @PatchMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Start sprint", description = "Start a planned sprint (Admin only)")
    public ResponseEntity<ApiResponse<SprintResponse>> startSprint(@PathVariable Long id) {
        SprintResponse response = sprintService.startSprint(id);
        return ResponseEntity.ok(ApiResponse.success("Sprint started successfully", response));
    }

    /**
     * Complete sprint
     * PATCH /api/sprints/{id}/complete
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Complete sprint", description = "Complete an active sprint (Admin only)")
    public ResponseEntity<ApiResponse<SprintResponse>> completeSprint(@PathVariable Long id) {
        SprintResponse response = sprintService.completeSprint(id);
        return ResponseEntity.ok(ApiResponse.success("Sprint completed successfully", response));
    }

    /**
     * Delete sprint
     * DELETE /api/sprints/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete sprint", description = "Delete a sprint (cannot delete active sprint)")
    public ResponseEntity<ApiResponse<Void>> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.ok(ApiResponse.success("Sprint deleted successfully", null));
    }
}

