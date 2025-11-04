package com.project.pmt.service;

import com.project.pmt.entity.Issue;
import com.project.pmt.enums.IssueStatus;
import com.project.pmt.enums.Priority;
import com.project.pmt.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final IssueRepository issueRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] exportIssuesToExcel(
            Long projectId,
            Long assigneeId,
            Long reporterId,
            IssueStatus status,
            Priority priority,
            Long sprintId,
            String search) throws IOException {

        log.info("Exporting issues to Excel");

        List<Issue> issues = issueRepository.searchIssues(
                projectId, assigneeId, reporterId, status, priority, sprintId, search, Pageable.unpaged()
        ).getContent();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Issues");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {
                    "Issue Key", "Title", "Type", "Priority", "Status",
                    "Assignee", "Reporter", "Sprint", "Created At", "Updated At"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (Issue issue : issues) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(issue.getIssueKey());
                row.createCell(1).setCellValue(issue.getTitle());
                row.createCell(2).setCellValue(issue.getType().name());
                row.createCell(3).setCellValue(issue.getPriority().name());
                row.createCell(4).setCellValue(issue.getStatus().name());
                row.createCell(5).setCellValue(issue.getAssignee() != null ? issue.getAssignee().getFullName() : "Unassigned");
                row.createCell(6).setCellValue(issue.getReporter() != null ? issue.getReporter().getFullName() : "");
                row.createCell(7).setCellValue(issue.getSprint() != null ? issue.getSprint().getName() : "No Sprint");
                row.createCell(8).setCellValue(issue.getCreatedAt().format(DATE_FORMATTER));
                row.createCell(9).setCellValue(issue.getUpdatedAt().format(DATE_FORMATTER));
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            log.info("Issues exported successfully. Count: {}", issues.size());
            return out.toByteArray();
        }
    }

    public String exportIssuesToCSV(
            Long projectId,
            Long assigneeId,
            Long reporterId,
            IssueStatus status,
            Priority priority,
            Long sprintId,
            String search) {

        log.info("Exporting issues to CSV");

        List<Issue> issues = issueRepository.searchIssues(
                projectId, assigneeId, reporterId, status, priority, sprintId, search, Pageable.unpaged()
        ).getContent();

        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("Issue Key,Title,Type,Priority,Status,Assignee,Reporter,Sprint,Created At,Updated At\n");

        // Data rows
        for (Issue issue : issues) {
            csv.append(escapeCsv(issue.getIssueKey())).append(",");
            csv.append(escapeCsv(issue.getTitle())).append(",");
            csv.append(escapeCsv(issue.getType().name())).append(",");
            csv.append(escapeCsv(issue.getPriority().name())).append(",");
            csv.append(escapeCsv(issue.getStatus().name())).append(",");
            csv.append(escapeCsv(issue.getAssignee() != null ? issue.getAssignee().getFullName() : "Unassigned")).append(",");
            csv.append(escapeCsv(issue.getReporter() != null ? issue.getReporter().getFullName() : "")).append(",");
            csv.append(escapeCsv(issue.getSprint() != null ? issue.getSprint().getName() : "No Sprint")).append(",");
            csv.append(escapeCsv(issue.getCreatedAt().format(DATE_FORMATTER))).append(",");
            csv.append(escapeCsv(issue.getUpdatedAt().format(DATE_FORMATTER))).append("\n");
        }

        log.info("Issues exported successfully. Count: {}", issues.size());
        return csv.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
