package com.project.pmt.repository;

import com.project.pmt.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIssueId(Long issueId);

    Page<Comment> findByIssueId(Long issueId, Pageable pageable);

    List<Comment> findByUserId(Long userId);

    Long countByIssueId(Long issueId);
}
