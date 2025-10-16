package com.project.pmt.service;

import com.project.pmt.dto.request.CreateIssueRequest;
import com.project.pmt.dto.response.IssueResponse;
import com.project.pmt.entity.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssueService {
//    private final ProjectService projectService;


    public IssueResponse createIssue(CreateIssueRequest request) {
        log.info("Creating issue with title: {}", request.getTitle());
//        Project project =
        return null;
    }
}
