package com.project.pmt.repository;

import com.project.pmt.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByIssueId(Long issueId);

    List<Attachment> findByUploadedById(Long userId);

    Long countByIssueId(Long issueId);
}
