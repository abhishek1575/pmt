package com.project.pmt.controller;
import com.project.pmt.dto.response.ApiResponse;
import com.project.pmt.dto.response.AttachmentResponse;
import com.project.pmt.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Attachment Controller
 * Handles file upload/download operations for issues
 */
@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Attachments", description = "File attachment management endpoints")
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * Upload file attachment to an issue
     * POST /api/attachments/issue/{issueId}
     */
    @PostMapping(value = "/issue/{issueId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload attachment", description = "Upload a file attachment to an issue")
    public ResponseEntity<ApiResponse<AttachmentResponse>> uploadAttachment(
            @PathVariable Long issueId,
            @RequestParam("file") MultipartFile file) {
        AttachmentResponse response = attachmentService.uploadAttachment(issueId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("File uploaded successfully", response));
    }

    /**
     * Get attachment by ID
     * GET /api/attachments/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get attachment by ID", description = "Get attachment metadata by ID")
    public ResponseEntity<ApiResponse<AttachmentResponse>> getAttachmentById(@PathVariable Long id) {
        AttachmentResponse response = attachmentService.getAttachmentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all attachments for an issue
     * GET /api/attachments/issue/{issueId}
     */
    @GetMapping("/issue/{issueId}")
    @Operation(summary = "Get attachments by issue", description = "Get all attachments for a specific issue")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> getAttachmentsByIssue(
            @PathVariable Long issueId) {
        List<AttachmentResponse> response = attachmentService.getAttachmentsByIssue(issueId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Delete attachment
     * DELETE /api/attachments/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attachment", description = "Delete a file attachment")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.ok(ApiResponse.success("Attachment deleted successfully", null));
    }
}
