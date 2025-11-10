package com.project.pmt.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard Error Response DTO
 *
 * Provides consistent error response format for all API endpoints.
 * Used by GlobalExceptionHandler to format all exceptions.
 *
 * Compatible with:
 * - GlobalExceptionHandler
 * - All custom exceptions (ResourceNotFoundException, BadRequestException, etc.)
 * - Spring validation errors
 * - Spring Security errors
 *
 * @author Project Management Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Timestamp when error occurred
     * Format: yyyy-MM-dd'T'HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * HTTP status code
     * Examples: 400, 401, 403, 404, 409, 500
     */
    private int status;

    /**
     * Error type/category
     * Examples: "Bad Request", "Not Found", "Unauthorized"
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * API endpoint path where error occurred
     * Example: "/api/projects/123"
     */
    private String path;

    /**
     * Field-level validation errors
     * Key: field name, Value: error message
     */
    private Map<String, String> validationErrors;

    /**
     * Custom error code for client handling
     */
    private String errorCode;

    /**
     * Trace ID for debugging
     */
    private String traceId;

    /**
     * Additional debug information
     */
    private String debugInfo;

    /**
     * Stack trace (development only)
     */
    private String stackTrace;
}