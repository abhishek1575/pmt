package com.project.pmt.mapper;

import com.project.pmt.dto.response.CommentResponse;
import com.project.pmt.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentResponse toResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentResponse.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .user(userMapper.toResponse(comment.getUser()))
                .issueId(comment.getIssue().getId())
                .edited(comment.getEdited())
                .editedAt(comment.getEditedAt())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
