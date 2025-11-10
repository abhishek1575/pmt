package com.project.pmt.controller;

import com.project.pmt.enums.IssueStatus;
import com.project.pmt.enums.Priority;
import com.project.pmt.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Export Controller
 * Handles data export to various formats (Excel, CSV)
 */
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Export", description = "Data export endpoints")
public class ExportController {

    private final ExportService exportService;

    /**
     * Export issues to Excel
     * GET /api/export/issues/excel?projectId=1&status=TODO
     */
    @GetMapping("/issues/excel")
    @Operation(summary = "Export to Excel", description = "Export filtered issues to Excel file")
    public ResponseEntity<byte[]> exportIssuesToExcel(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long reporterId,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long sprintId,
            @RequestParam(required = false) String q) throws IOException {

        byte[] excelData = exportService.exportIssuesToExcel(
                projectId, assigneeId, reporterId, status, priority, sprintId, q
        );

        String filename = generateFilename("issues", "xlsx");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    /**
     * Export issues to CSV
     * GET /api/export/issues/csv?projectId=1&status=TODO
     */
    @GetMapping("/issues/csv")
    @Operation(summary = "Export to CSV", description = "Export filtered issues to CSV file")
    public ResponseEntity<String> exportIssuesToCSV(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long reporterId,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long sprintId,
            @RequestParam(required = false) String q) {

        String csvData = exportService.exportIssuesToCSV(
                projectId, assigneeId, reporterId, status, priority, sprintId, q
        );

        String filename = generateFilename("issues", "csv");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(csvData.length());

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    /**
     * Generate filename with timestamp
     */
    private String generateFilename(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_%s.%s", prefix, timestamp, extension);
    }
}