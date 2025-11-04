package com.project.pmt.service;

import com.project.pmt.dto.response.AttachmentResponse;
import com.project.pmt.entity.Attachment;
import com.project.pmt.entity.Issue;
import com.project.pmt.entity.User;
import com.project.pmt.exceptions.FileUploadException;
import com.project.pmt.exceptions.ResourceNotFoundException;
import com.project.pmt.mapper.AttachmentMapper;
import com.project.pmt.repository.AttachmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final IssueService issueService;
    private final UserService userService;
    private final AttachmentMapper attachmentMapper;
    private final S3Client s3Client;

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${aws.s3.enabled:false}")
    private boolean s3Enabled;

    @Value("${aws.s3.bucket:}")
    private String s3Bucket;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    @Transactional
    public AttachmentResponse uploadAttachment(Long issueId, MultipartFile file) {
        log.info("Uploading attachment for issue: {}", issueId);

        if (file.isEmpty()) {
            throw new FileUploadException("File is empty", file.getOriginalFilename());
        }

        if (file.getSize() > maxFileSize) {
            throw new FileUploadException("File size exceeds maximum allowed size", file.getOriginalFilename());
        }

        Issue issue = issueService.findIssueEntityById(issueId);
        User user = userService.getCurrentUserEntity();

        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        String fileUrl;

        try {
            if (s3Enabled) {
                fileUrl = uploadToS3(file, filename);
            } else {
                fileUrl = uploadToLocal(file, filename);
            }
        } catch (IOException e) {
            log.error("Error uploading file", e);
            throw new FileUploadException("Failed to upload file", filename, e);
        }

        Attachment attachment = new Attachment();
        attachment.setIssue(issue);
        attachment.setFilename(originalFilename);
        attachment.setUrl(fileUrl);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setUploadedBy(user);

        attachment = attachmentRepository.save(attachment);
        log.info("Attachment uploaded successfully: {}", attachment.getId());

        return attachmentMapper.toResponse(attachment);
    }

    private String uploadToLocal(MultipartFile file, String filename) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + filename;
    }

    private String uploadToS3(MultipartFile file, String filename) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(filename)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return String.format("https://%s.s3.amazonaws.com/%s", s3Bucket, filename);
    }

    public AttachmentResponse getAttachmentById(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", id));
        return attachmentMapper.toResponse(attachment);
    }

    public List<AttachmentResponse> getAttachmentsByIssue(Long issueId) {
        return attachmentRepository.findByIssueId(issueId).stream()
                .map(attachmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAttachment(Long id) {
        log.info("Deleting attachment: {}", id);

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", id));

        // Delete file from storage
        try {
            if (s3Enabled) {
                // Delete from S3 (implement S3 delete)
            } else {
                Path filePath = Paths.get(uploadDir).resolve(attachment.getFilename());
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.error("Error deleting file", e);
        }

        attachmentRepository.delete(attachment);
        log.info("Attachment deleted successfully: {}", id);
    }
}
