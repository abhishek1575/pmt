package com.project.pmt.mapper;

import com.project.pmt.dto.response.AttachmentResponse;
import com.project.pmt.entity.Attachment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttachmentMapper {
    private final UserMapper userMapper;

    public AttachmentResponse toResponse(Attachment attachment){
        if (attachment == null){
            return null;
        }
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .filename(attachment.getFilename())
                .url(attachment.getUrl())
                .contentType(attachment.getContentType())
                .size(attachment.getSize())
                .uploadedBy(userMapper.toResponse(attachment.getUploadedBy()))
                .issueId(attachment.getIssue().getId())
                .createdAt(attachment.getCreatedAt())
                .build();
    }

}
