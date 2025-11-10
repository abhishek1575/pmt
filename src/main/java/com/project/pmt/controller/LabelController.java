package com.project.pmt.controller;

import com.project.pmt.dto.request.CreateLabelRequest;
import com.project.pmt.dto.request.UpdateLabelRequest;
import com.project.pmt.dto.response.ApiResponse;
import com.project.pmt.dto.response.LabelResponse;
import com.project.pmt.service.LabelService;
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
 * Label Controller
 * Handles label/tag management for categorizing issues
 */
@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Labels", description = "Label/Tag management endpoints")
public class LabelController {

    private final LabelService labelService;

    /**
     * Create a new label
     * POST /api/labels
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create label", description = "Create a new label/tag (Admin only)")
    public ResponseEntity<ApiResponse<LabelResponse>> createLabel(
            @Valid @RequestBody CreateLabelRequest request) {
        LabelResponse response = labelService.createLabel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Label created successfully", response));
    }

    /**
     * Get label by ID
     * GET /api/labels/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get label by ID", description = "Get label details by ID")
    public ResponseEntity<ApiResponse<LabelResponse>> getLabelById(@PathVariable Long id) {
        LabelResponse response = labelService.getLabelById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all labels
     * GET /api/labels
     */
    @GetMapping
    @Operation(summary = "Get all labels", description = "Get list of all labels")
    public ResponseEntity<ApiResponse<List<LabelResponse>>> getAllLabels() {
        List<LabelResponse> response = labelService.getAllLabels();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search labels by name
     * GET /api/labels/search?name=bug
     */
    @GetMapping("/search")
    @Operation(summary = "Search labels", description = "Search labels by name")
    public ResponseEntity<ApiResponse<List<LabelResponse>>> searchLabels(@RequestParam String name) {
        List<LabelResponse> response = labelService.searchLabels(name);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update label
     * PUT /api/labels/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update label", description = "Update label details (Admin only)")
    public ResponseEntity<ApiResponse<LabelResponse>> updateLabel(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLabelRequest request) {
        LabelResponse response = labelService.updateLabel(id, request);
        return ResponseEntity.ok(ApiResponse.success("Label updated successfully", response));
    }

    /**
     * Delete label
     * DELETE /api/labels/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete label", description = "Delete a label (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
        return ResponseEntity.ok(ApiResponse.success("Label deleted successfully", null));
    }
}