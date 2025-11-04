package com.project.pmt.service;


import com.project.pmt.dto.request.CreateIssueRequest;
import com.project.pmt.dto.request.UpdateIssueRequest;
import com.project.pmt.dto.response.IssueResponse;
import com.project.pmt.dto.response.PageResponse;
import com.project.pmt.entity.*;
import com.project.pmt.enums.IssueStatus;
import com.project.pmt.enums.NotificationType;
import com.project.pmt.enums.Priority;
import com.project.pmt.exceptions.ResourceNotFoundException;
import com.project.pmt.mapper.IssueMapper;
import com.project.pmt.repository.IssueRepository;
import com.project.pmt.repository.LabelRepository;
import com.project.pmt.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final SprintRepository sprintRepository;
    private final LabelRepository labelRepository;
    private final NotificationService notificationService;
    private final IssueMapper issueMapper;

    @Transactional
    public IssueResponse createIssue(CreateIssueRequest request) {
        log.info("Creating new issue for project: {}", request.getProjectId());

        Project project = projectService.findProjectEntityById(request.getProjectId());
        User reporter = userService.getCurrentUserEntity();

        // Generate issue key
        String issueKey = generateIssueKey(project);

        Issue issue = new Issue();
        issue.setProject(project);
        issue.setIssueKey(issueKey);
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setType(request.getIssueType());
        issue.setPriority(request.getPriority());
        issue.setStatus(request.getStatus() != null ? request.getStatus() : IssueStatus.TODO);
        issue.setReporter(reporter);
        issue.setEstimatedHours(request.getEstimateHours());
        issue.setDueDate(request.getDueDate());
        issue.setLoggedHours(0);

        // Set assignee
        if (request.getAssigneeId() != null) {
            User assignee = userService.findUserEntityById(request.getAssigneeId());
            issue.setAssignee(assignee);
        }

        // Set sprint
        if (request.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(request.getSprintId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sprint", "id", request.getSprintId()));
            issue.setSprint(sprint);
        }

        // Set labels
        if (request.getLabelIds() != null && !request.getLabelIds().isEmpty()) {
            Set<Label> labels = new HashSet<>();
            for (Long labelId : request.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));
                labels.add(label);
            }
            issue.setLabels(labels);
        }

        // Set parent issue
        if (request.getParentIssueId() != null) {
            Issue parentIssue = issueRepository.findById(request.getParentIssueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", request.getParentIssueId()));
            issue.setParentIssue(parentIssue);
        }

        // Set board order
        Integer maxOrder = issueRepository.findMaxBoardOrder(project.getId(), issue.getStatus());
        issue.setBoardOrder(maxOrder != null ? maxOrder + 1 : 0);

        issue = issueRepository.save(issue);
        log.info("Issue created successfully: {}", issue.getIssueKey());

        // Send notification to assignee
        if (issue.getAssignee() != null) {
            notificationService.createNotification(
                    issue.getAssignee(),
                    NotificationType.ASSIGNMENT,
                    String.format("You have been assigned to %s: %s", issue.getIssueKey(), issue.getTitle()),
                    "/issues/" + issue.getId(),
                    issue
            );
        }

        return issueMapper.toResponse(issue);
    }

    public IssueResponse getIssueById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));
        return issueMapper.toResponse(issue);
    }

    public IssueResponse getIssueByKey(String issueKey) {
        Issue issue = issueRepository.findByIssueKey(issueKey.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "key", issueKey));
        return issueMapper.toResponse(issue);
    }

    public List<IssueResponse> getIssuesByProject(Long projectId) {
        return issueRepository.findByProjectId(projectId).stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<IssueResponse> getIssuesByProjectPaginated(Long projectId, Pageable pageable) {
        Page<Issue> issuePage = issueRepository.findByProjectId(projectId, pageable);
        return issueMapper.toPageResponse(issuePage);
    }

    public List<IssueResponse> getIssuesByStatus(Long projectId, IssueStatus status) {
        return issueRepository.findByProjectIdAndStatus(projectId, status).stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<IssueResponse> getMyIssues() {
        User currentUser = userService.getCurrentUserEntity();
        return issueRepository.findByAssigneeId(currentUser.getId()).stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<IssueResponse> getIssuesBySprint(Long sprintId) {
        return issueRepository.findBySprintId(sprintId).stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<IssueResponse> searchIssues(
            Long projectId,
            Long assigneeId,
            Long reporterId,
            IssueStatus status,
            Priority priority,
            Long sprintId,
            String search,
            Pageable pageable) {

        Page<Issue> issuePage = issueRepository.searchIssues(
                projectId, assigneeId, reporterId, status, priority, sprintId, search, pageable
        );
        return issueMapper.toPageResponse(issuePage);
    }

    @Transactional
    public IssueResponse updateIssue(Long id, UpdateIssueRequest request) {
        log.info("Updating issue: {}", id);

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));

        IssueStatus oldStatus = issue.getStatus();
        User oldAssignee = issue.getAssignee();

        if (request.getTitle() != null) {
            issue.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            issue.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            issue.setType(request.getType());
        }
        if (request.getPriority() != null) {
            issue.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            issue.setStatus(request.getStatus());

            // Set resolved time if status is DONE
            if (request.getStatus() == IssueStatus.DONE && oldStatus != IssueStatus.DONE) {
                issue.setResolvedAt(LocalDateTime.now());
            } else if (request.getStatus() != IssueStatus.DONE && oldStatus == IssueStatus.DONE) {
                issue.setResolvedAt(null);
            }
        }
        if (request.getAssigneeId() != null) {
            User assignee = userService.findUserEntityById(request.getAssigneeId());
            issue.setAssignee(assignee);
        }
        if (request.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(request.getSprintId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sprint", "id", request.getSprintId()));
            issue.setSprint(sprint);
        }
        if (request.getDueDate() != null) {
            issue.setDueDate(request.getDueDate());
        }
        if (request.getEstimatedHours() != null) {
            issue.setEstimatedHours(request.getEstimatedHours());
        }
        if (request.getLoggedHours() != null) {
            issue.setLoggedHours(request.getLoggedHours());
        }
        if (request.getBoardOrder() != null) {
            issue.setBoardOrder(request.getBoardOrder());
        }

        // Update labels
        if (request.getLabelIds() != null) {
            Set<Label> labels = new HashSet<>();
            for (Long labelId : request.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));
                labels.add(label);
            }
            issue.setLabels(labels);
        }

        issue = issueRepository.save(issue);
        log.info("Issue updated successfully: {}", issue.getIssueKey());

        // Send notifications
        if (request.getAssigneeId() != null && !request.getAssigneeId().equals(oldAssignee != null ? oldAssignee.getId() : null)) {
            notificationService.createNotification(
                    issue.getAssignee(),
                    NotificationType.ASSIGNMENT,
                    String.format("You have been assigned to %s: %s", issue.getIssueKey(), issue.getTitle()),
                    "/issues/" + issue.getId(),
                    issue
            );
        }

        if (request.getStatus() != null && !request.getStatus().equals(oldStatus)) {
            if (issue.getAssignee() != null) {
                notificationService.createNotification(
                        issue.getAssignee(),
                        NotificationType.STATUS_CHANGE,
                        String.format("%s status changed from %s to %s", issue.getIssueKey(), oldStatus, issue.getStatus()),
                        "/issues/" + issue.getId(),
                        issue
                );
            }
        }

        return issueMapper.toResponse(issue);
    }

    @Transactional
    public IssueResponse updateIssueStatus(Long id, IssueStatus newStatus) {
        log.info("Updating issue status: {} -> {}", id, newStatus);

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));

        IssueStatus oldStatus = issue.getStatus();
        issue.setStatus(newStatus);

        // Set resolved time if status is DONE
        if (newStatus == IssueStatus.DONE && oldStatus != IssueStatus.DONE) {
            issue.setResolvedAt(LocalDateTime.now());
        } else if (newStatus != IssueStatus.DONE && oldStatus == IssueStatus.DONE) {
            issue.setResolvedAt(null);
        }

        // Reset board order when status changes
        Integer maxOrder = issueRepository.findMaxBoardOrder(issue.getProject().getId(), newStatus);
        issue.setBoardOrder(maxOrder != null ? maxOrder + 1 : 0);

        issue = issueRepository.save(issue);
        log.info("Issue status updated successfully: {}", issue.getIssueKey());

        // Send notification
        if (issue.getAssignee() != null) {
            notificationService.createNotification(
                    issue.getAssignee(),
                    NotificationType.STATUS_CHANGE,
                    String.format("%s status changed from %s to %s", issue.getIssueKey(), oldStatus, newStatus),
                    "/issues/" + issue.getId(),
                    issue
            );
        }

        return issueMapper.toResponse(issue);
    }

    @Transactional
    public void updateIssueBoardOrder(Long id, Integer newOrder, IssueStatus status) {
        log.info("Updating issue board order: {} -> order: {}, status: {}", id, newOrder, status);

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));

        IssueStatus oldStatus = issue.getStatus();

        // Update status if changed
        if (status != null && !status.equals(oldStatus)) {
            issue.setStatus(status);

            if (status == IssueStatus.DONE && oldStatus != IssueStatus.DONE) {
                issue.setResolvedAt(LocalDateTime.now());
            } else if (status != IssueStatus.DONE && oldStatus == IssueStatus.DONE) {
                issue.setResolvedAt(null);
            }
        }

        issue.setBoardOrder(newOrder);
        issueRepository.save(issue);
        log.info("Issue board order updated successfully");
    }

    @Transactional
    public void deleteIssue(Long id) {
        log.info("Deleting issue: {}", id);

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));

        issueRepository.delete(issue);
        log.info("Issue deleted successfully: {}", id);
    }

    @Transactional
    public void assignIssue(Long issueId, Long userId) {
        log.info("Assigning issue {} to user {}", issueId, userId);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        User assignee = userService.findUserEntityById(userId);

        User oldAssignee = issue.getAssignee();
        issue.setAssignee(assignee);
        issueRepository.save(issue);

        // Send notification
        if (!assignee.equals(oldAssignee)) {
            notificationService.createNotification(
                    assignee,
                    NotificationType.ASSIGNMENT,
                    String.format("You have been assigned to %s: %s", issue.getIssueKey(), issue.getTitle()),
                    "/issues/" + issue.getId(),
                    issue
            );
        }

        log.info("Issue assigned successfully");
    }

    @Transactional
    public void unassignIssue(Long issueId) {
        log.info("Unassigning issue: {}", issueId);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        issue.setAssignee(null);
        issueRepository.save(issue);
        log.info("Issue unassigned successfully");
    }

    @Transactional
    public void addLabelToIssue(Long issueId, Long labelId) {
        log.info("Adding label {} to issue {}", labelId, issueId);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));

        issue.getLabels().add(label);
        issueRepository.save(issue);
        log.info("Label added to issue successfully");
    }

    @Transactional
    public void removeLabelFromIssue(Long issueId, Long labelId) {
        log.info("Removing label {} from issue {}", labelId, issueId);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));

        issue.getLabels().remove(label);
        issueRepository.save(issue);
        log.info("Label removed from issue successfully");
    }

    private String generateIssueKey(Project project) {
        Integer maxNumber = issueRepository.findMaxIssueNumber(project.getKey());
        int nextNumber = (maxNumber != null ? maxNumber : 0) + 1;
        return project.getKey() + "-" + nextNumber;
    }

    public Long countIssuesByStatus(Long projectId, IssueStatus status) {
        return issueRepository.countByProjectIdAndStatus(projectId, status);
    }

    public Issue findIssueEntityById(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", id));
    }
}