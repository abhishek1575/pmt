package com.project.pmt.controller;

import com.project.pmt.dto.request.CreateCommentRequest;
import com.project.pmt.dto.request.UpdateCommentRequest;
import com.project.pmt.dto.response.ApiResponse;
import com.project.pmt.dto.response.CommentResponse;
import com.project.pmt.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Comment Controller
 * Handles comment operations on issues
 */
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {

    private final CommentService commentService;

    /**
     * Create comment on an issue
     * POST /api/comments/issue/{issueId}
     */
    @PostMapping("/issue/{issueId}")
    @Operation(summary = "Create comment", description = "Add a comment to an issue")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long issueId,
            @Valid @RequestBody CreateCommentRequest request) {
        CommentResponse response = commentService.createComment(issueId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", response));
    }

    /**
     * Get comment by ID
     * GET /api/comments/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID", description = "Get comment details by ID")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(@PathVariable Long id) {
        CommentResponse response = commentService.getCommentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all comments for an issue
     * GET /api/comments/issue/{issueId}
     */
    @GetMapping("/issue/{issueId}")
    @Operation(summary = "Get comments by issue", description = "Get all comments for a specific issue")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsByIssue(
            @PathVariable Long issueId) {
        List<CommentResponse> response = commentService.getCommentsByIssue(issueId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update comment
     * PUT /api/comments/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update comment", description = "Update comment content (own comments only)")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentRequest request) {
        CommentResponse response = commentService.updateComment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", response));
    }

    /**
     * Delete comment
     * DELETE /api/comments/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Delete a comment (own comments only)")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }
}