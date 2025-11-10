package com.project.pmt.service;


import com.project.pmt.dto.request.CreateCommentRequest;
import com.project.pmt.dto.request.UpdateCommentRequest;
import com.project.pmt.dto.response.CommentResponse;
import com.project.pmt.entity.Comment;
import com.project.pmt.entity.Issue;
import com.project.pmt.entity.User;
import com.project.pmt.enums.NotificationType;
import com.project.pmt.exceptions.ResourceNotFoundException;
import com.project.pmt.mapper.CommentMapper;
import com.project.pmt.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueService issueService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse createComment(Long issueId, CreateCommentRequest request) {
        log.info("Creating comment for issue: {}", issueId);

        Issue issue = issueService.findIssueEntityById(issueId);
        User user = userService.getCurrentUserEntity();

        Comment comment = new Comment();
        comment.setIssue(issue);
        comment.setUser(user);
        comment.setBody(request.getBody());
        comment.setEdited(false);

        comment = commentRepository.save(comment);
        log.info("Comment created successfully: {}", comment.getId());

        // Send notification to issue assignee if not the commenter
        if (issue.getAssignee() != null && !issue.getAssignee().equals(user)) {
            notificationService.createNotification(
                    issue.getAssignee(),
                    NotificationType.COMMENT,
                    String.format("%s commented on %s", user.getFullName(), issue.getIssueKey()),
                    "/issues/" + issue.getId(),
                    issue
            );
        }

        return commentMapper.toResponse(comment);
    }

    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        return commentMapper.toResponse(comment);
    }

    public List<CommentResponse> getCommentsByIssue(Long issueId) {
        return commentRepository.findByIssueId(issueId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse updateComment(Long id, UpdateCommentRequest request) {
        log.info("Updating comment: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        User currentUser = userService.getCurrentUserEntity();
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only edit your own comments");
        }

        comment.setBody(request.getBody());
        comment.setEdited(true);
        comment.setEditedAt(LocalDateTime.now());

        comment = commentRepository.save(comment);
        log.info("Comment updated successfully: {}", comment.getId());

        return commentMapper.toResponse(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        log.info("Deleting comment: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        User currentUser = userService.getCurrentUserEntity();
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
        log.info("Comment deleted successfully: {}", id);
    }
}
