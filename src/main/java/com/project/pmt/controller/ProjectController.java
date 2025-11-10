package com.project.pmt.controller;


import com.project.pmt.dto.request.CreateProjectRequest;
import com.project.pmt.dto.request.UpdateProjectRequest;
import com.project.pmt.dto.response.ApiResponse;
import com.project.pmt.dto.response.PageResponse;
import com.project.pmt.dto.response.ProjectResponse;
import com.project.pmt.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Project Controller
 * Handles all project-related HTTP requests
 * Base path: /api/projects
 *
 * @author Project Management Team
 * @version 1.0
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Projects", description = "Project management endpoints")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Create a new project
     * POST /api/projects
     *
     * Requires: ADMIN or SUPER_ADMIN role
     *
     * @param request CreateProjectRequest containing project details
     * @return ApiResponse with created ProjectResponse
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Create a new project",
            description = "Create a new project with name, unique key, description, and team members. Admin only."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Project created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody
            @Parameter(description = "Project creation request with name, key, and description", required = true)
            CreateProjectRequest request) {

        log.info("REST Request: Create new project with key: {}", request.getKey());
        ProjectResponse response = projectService.createProject(request);
        log.info("REST Response: Project created successfully with ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", response));
    }

    /**
     * Get project by ID
     * GET /api/projects/{id}
     *
     * @param id Project ID
     * @return ApiResponse with ProjectResponse
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get project by ID",
            description = "Retrieve detailed project information including members and metadata"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(
            @PathVariable
            @Parameter(description = "Unique project ID", required = true)
            Long id) {

        log.debug("REST Request: Get project by ID: {}", id);
        ProjectResponse response = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get project by unique key
     * GET /api/projects/key/{key}
     *
     * @param key Project key (e.g., PROJ, DEV, TEST)
     * @return ApiResponse with ProjectResponse
     */
    @GetMapping("/key/{key}")
    @Operation(
            summary = "Get project by key",
            description = "Retrieve project using its unique key identifier (e.g., PROJ, DEV)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectByKey(
            @PathVariable
            @Parameter(description = "Unique project key (e.g., PROJ, DEV)", required = true)
            String key) {

        log.debug("REST Request: Get project by key: {}", key);
        ProjectResponse response = projectService.getProjectByKey(key);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all active projects (non-paginated)
     * GET /api/projects
     *
     * @return ApiResponse with List of ProjectResponse
     */
    @GetMapping
    @Operation(
            summary = "Get all active projects",
            description = "Retrieve complete list of all active (non-archived) projects"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {
        log.debug("REST Request: Get all projects");
        List<ProjectResponse> response = projectService.getAllProjects();
        log.debug("REST Response: Retrieved {} projects", response.size());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all projects with pagination and sorting
     * GET /api/projects/paginated?page=0&size=20&sortBy=createdAt&sortDirection=DESC
     *
     * @param page Page number (0-indexed, default: 0)
     * @param size Number of items per page (default: 20)
     * @param sortBy Field to sort by (default: createdAt)
     * @param sortDirection Sort direction ASC or DESC (default: DESC)
     * @return ApiResponse with PageResponse of ProjectResponse
     */
    @GetMapping("/paginated")
    @Operation(
            summary = "Get projects with pagination",
            description = "Retrieve projects with pagination and sorting. Supports sorting by any project field."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<ProjectResponse>>> getAllProjectsPaginated(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Page number (0-indexed)", example = "0")
            int page,

            @RequestParam(defaultValue = "20")
            @Parameter(description = "Number of items per page", example = "20")
            int size,

            @RequestParam(defaultValue = "createdAt")
            @Parameter(description = "Field to sort by", example = "name")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            @Parameter(description = "Sort direction (ASC or DESC)", example = "DESC")
            String sortDirection) {

        log.debug("REST Request: Get paginated projects - page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, sortDirection);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PageResponse<ProjectResponse> response = projectService.getAllProjectsPaginated(pageable);
        log.debug("REST Response: Retrieved page {} of {} with {} projects",
                response.getPageNumber(), response.getTotalPages(), response.getContent().size());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get projects where current user is a member
     * GET /api/projects/my
     *
     * @return ApiResponse with List of ProjectResponse
     */
    @GetMapping("/my")
    @Operation(
            summary = "Get my projects",
            description = "Retrieve all projects where the authenticated user is a team member"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getMyProjects() {
        log.debug("REST Request: Get my projects");
        List<ProjectResponse> response = projectService.getMyProjects();
        log.debug("REST Response: User has access to {} projects", response.size());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search projects by name, key, or description
     * GET /api/projects/search?q=searchTerm&page=0&size=20
     *
     * @param q Search query string
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @return ApiResponse with PageResponse of ProjectResponse
     */
    @GetMapping("/search")
    @Operation(
            summary = "Search projects",
            description = "Search projects by name, key, or description with pagination support"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<ProjectResponse>>> searchProjects(
            @RequestParam
            @Parameter(description = "Search query string", required = true, example = "development")
            String q,

            @RequestParam(defaultValue = "0")
            @Parameter(description = "Page number", example = "0")
            int page,

            @RequestParam(defaultValue = "20")
            @Parameter(description = "Page size", example = "20")
            int size) {

        log.debug("REST Request: Search projects with query: '{}', page: {}, size: {}", q, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ProjectResponse> response = projectService.searchProjects(q, pageable);
        log.debug("REST Response: Found {} projects matching '{}'", response.getTotalElements(), q);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update project details
     * PUT /api/projects/{id}
     *
     * Requires: ADMIN or SUPER_ADMIN role
     *
     * @param id Project ID
     * @param request UpdateProjectRequest with new details
     * @return ApiResponse with updated ProjectResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Update project",
            description = "Update project details including name, description, lead, and members. Admin only."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable
            @Parameter(description = "Project ID", required = true)
            Long id,

            @Valid @RequestBody
            @Parameter(description = "Project update request", required = true)
            UpdateProjectRequest request) {

        log.info("REST Request: Update project ID: {}", id);
        ProjectResponse response = projectService.updateProject(id, request);
        log.info("REST Response: Project updated successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", response));
    }

    /**
     * Add member to project
     * POST /api/projects/{projectId}/members/{userId}
     *
     * Requires: ADMIN or SUPER_ADMIN role
     *
     * @param projectId Project ID
     * @param userId User ID to add as member
     * @return ApiResponse with success message
     */
    @PostMapping("/{projectId}/members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Add member to project",
            description = "Add a user to project team members. Admin only."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Member added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "User already a member or invalid data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project or user not found")
    })
    public ResponseEntity<ApiResponse<Void>> addMemberToProject(
            @PathVariable
            @Parameter(description = "Project ID", required = true)
            Long projectId,

            @PathVariable
            @Parameter(description = "User ID to add", required = true)
            Long userId) {

        log.info("REST Request: Add user {} to project {}", userId, projectId);
        projectService.addMemberToProject(projectId, userId);
        log.info("REST Response: User {} added to project {} successfully", userId, projectId);

        return ResponseEntity.ok(ApiResponse.success("Member added to project successfully", null));
    }

    /**
     * Remove member from project
     * DELETE /api/projects/{projectId}/members/{userId}
     *
     * Requires: ADMIN or SUPER_ADMIN role
     *
     * @param projectId Project ID
     * @param userId User ID to remove
     * @return ApiResponse with success message
     */
    @DeleteMapping("/{projectId}/members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Remove member from project",
            description = "Remove a user from project team. Cannot remove project lead. Admin only."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Member removed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot remove project lead"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project or user not found")
    })
    public ResponseEntity<ApiResponse<Void>> removeMemberFromProject(
            @PathVariable
            @Parameter(description = "Project ID", required = true)
            Long projectId,

            @PathVariable
            @Parameter(description = "User ID to remove", required = true)
            Long userId) {

        log.info("REST Request: Remove user {} from project {}", userId, projectId);
        projectService.removeMemberFromProject(projectId, userId);
        log.info("REST Response: User {} removed from project {} successfully", userId, projectId);

        return ResponseEntity.ok(ApiResponse.success("Member removed from project successfully", null));
    }

    /**
     * Archive project (soft delete)
     * PATCH /api/projects/{id}/archive
     *
     * Requires: ADMIN or SUPER_ADMIN role
     *
     * @param id Project ID
     * @return ApiResponse with success message
     */
    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Archive project",
            description = "Archive a project (soft delete). Project will be hidden from normal views. Admin only."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project archived successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<Void>> archiveProject(
            @PathVariable
            @Parameter(description = "Project ID", required = true)
            Long id) {

        log.info("REST Request: Archive project ID: {}", id);
        projectService.archiveProject(id);
        log.info("REST Response: Project {} archived successfully", id);

        return ResponseEntity.ok(ApiResponse.success("Project archived successfully", null));
    }

    /**
     * Unarchive project
     * PATCH /api/projects/{id}/unarchive
     *
     * Requires: ADMIN or SUPER_ADMIN role
     *
     * @param id Project ID
     * @return ApiResponse with success message
     */
    @PatchMapping("/{id}/unarchive")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Unarchive project",
            description = "Restore an archived project to active status. Admin only."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project unarchived successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<Void>> unarchiveProject(
            @PathVariable
            @Parameter(description = "Project ID", required = true)
            Long id) {

        log.info("REST Request: Unarchive project ID: {}", id);
        projectService.unarchiveProject(id);
        log.info("REST Response: Project {} unarchived successfully", id);

        return ResponseEntity.ok(ApiResponse.success("Project unarchived successfully", null));
    }

    /**
     * Permanently delete project
     * DELETE /api/projects/{id}
     *
     * Requires: SUPER_ADMIN role only
     *
     * @param id Project ID
     * @return ApiResponse with success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Delete project permanently",
            description = "Permanently delete a project and all associated data. This action cannot be undone. Super Admin only."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Super Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable
            @Parameter(description = "Project ID", required = true)
            Long id) {

        log.warn("REST Request: Permanently delete project ID: {}", id);
        projectService.deleteProject(id);
        log.warn("REST Response: Project {} deleted permanently", id);

        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }

    /**
     * Check if user is a member of the project
     * GET /api/projects/{projectId}/members/{userId}/check
     *
     * @param projectId Project ID
     * @param userId User ID
     * @return ApiResponse with boolean result
     */
    @GetMapping("/{projectId}/members/{userId}/check")
    @Operation(
            summary = "Check project membership",
            description = "Verify if a specific user is a member of the project"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<Boolean>> isUserMemberOfProject(
            @PathVariable
            @Parameter(description = "Project ID", required = true)
            Long projectId,

            @PathVariable
            @Parameter(description = "User ID to check", required = true)
            Long userId) {

        log.debug("REST Request: Check if user {} is member of project {}", userId, projectId);
        boolean isMember = projectService.isUserMemberOfProject(projectId, userId);
        log.debug("REST Response: User {} membership in project {}: {}", userId, projectId, isMember);

        return ResponseEntity.ok(ApiResponse.success(isMember));
    }

    /**
     * Check if user is the project lead
     * GET /api/projects/{projectId}/lead/{userId}/check
     *
     * @param projectId Project ID
     * @param userId User ID
     * @return ApiResponse with boolean result
     */
    @GetMapping("/{projectId}/lead/{userId}/check")
    @Operation(
            summary = "Check if user is project lead",
            description = "Verify if a specific user is the project lead"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<Boolean>> isUserProjectLead(
            @PathVariable
            @Parameter(description = "Project ID", required = true)
            Long projectId,

            @PathVariable
            @Parameter(description = "User ID to check", required = true)
            Long userId) {

        log.debug("REST Request: Check if user {} is lead of project {}", userId, projectId);
        boolean isLead = projectService.isUserProjectLead(projectId, userId);
        log.debug("REST Response: User {} lead status for project {}: {}", userId, projectId, isLead);

        return ResponseEntity.ok(ApiResponse.success(isLead));
    }
}