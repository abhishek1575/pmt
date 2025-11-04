package com.project.pmt.service;

import com.project.pmt.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueService issueService;
    private final UserService userService;
    private final NotificationService notificationService;
}
