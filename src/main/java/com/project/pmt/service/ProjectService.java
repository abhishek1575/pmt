package com.project.pmt.service;

import com.project.pmt.dto.request.CreateProjectRequest;
import com.project.pmt.dto.request.UpdateProjectRequest;
import com.project.pmt.dto.response.PageResponse;
import com.project.pmt.dto.response.ProjectResponse;
import com.project.pmt.entity.Project;
import com.project.pmt.entity.User;
import com.project.pmt.exceptions.BadRequestException;
import com.project.pmt.exceptions.DuplicateResourceException;
import com.project.pmt.exceptions.ResourceNotFoundException;
import com.project.pmt.mapper.ProjectMapper;
import com.project.pmt.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        log.info("Creating new project: {}", request.getName());

        // Validate project key uniqueness
        if (projectRepository.existsByKey(request.getKey().toUpperCase())) {
            throw new DuplicateResourceException("Project", "key");
        }

        User currentUser = userService.getCurrentUserEntity();

        Project project = new Project();
        project.setName(request.getName());
        project.setKey(request.getKey().toUpperCase());
        project.setDescription(request.getDescription());
        project.setIconUrl(request.getIconUrl());
        project.setArchived(false);

        // Set lead
        if (request.getLeadId() != null) {
            User lead = userService.findUserEntityById(request.getLeadId());
            project.setLead(lead);
        } else {
            project.setLead(currentUser);
        }

        // Add members
        Set<User> members = new HashSet<>();
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            for (Long memberId : request.getMemberIds()) {
                User member = userService.findUserEntityById(memberId);
                members.add(member);
            }
        }
        // Always add the lead as a member
        members.add(project.getLead());
        project.setMembers(members);

        project = projectRepository.save(project);
        log.info("Project created successfully: {}", project.getId());

        return projectMapper.toResponse(project);
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        return projectMapper.toResponse(project);
    }

    public ProjectResponse getProjectByKey(String key) {
        Project project = projectRepository.findByKey(key.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "key", key));
        return projectMapper.toResponse(project);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findByArchivedFalse().stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<ProjectResponse> getAllProjectsPaginated(Pageable pageable) {
        Page<Project> projectPage = projectRepository.findActiveProjects(pageable);
        return projectMapper.toPageResponse(projectPage);
    }

    public List<ProjectResponse> getMyProjects() {
        User currentUser = userService.getCurrentUserEntity();
        return projectRepository.findProjectsByMemberId(currentUser.getId()).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<ProjectResponse> searchProjects(String search, Pageable pageable) {
        Page<Project> projectPage = projectRepository.searchProjects(search, pageable);
        return projectMapper.toPageResponse(projectPage);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        log.info("Updating project: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getIconUrl() != null) {
            project.setIconUrl(request.getIconUrl());
        }
        if (request.getArchived() != null) {
            project.setArchived(request.getArchived());
        }
        if (request.getLeadId() != null) {
            User lead = userService.findUserEntityById(request.getLeadId());
            project.setLead(lead);
        }

        // Update members
        if (request.getMemberIds() != null) {
            Set<User> members = new HashSet<>();
            for (Long memberId : request.getMemberIds()) {
                User member = userService.findUserEntityById(memberId);
                members.add(member);
            }
            // Always include the lead
            members.add(project.getLead());
            project.setMembers(members);
        }

        project = projectRepository.save(project);
        log.info("Project updated successfully: {}", project.getId());

        return projectMapper.toResponse(project);
    }

    @Transactional
    public void addMemberToProject(Long projectId, Long userId) {
        log.info("Adding user {} to project {}", userId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        User user = userService.findUserEntityById(userId);

        if (project.getMembers().contains(user)) {
            throw new BadRequestException("User is already a member of this project");
        }

        project.getMembers().add(user);
        projectRepository.save(project);
        log.info("User added to project successfully");
    }

    @Transactional
    public void removeMemberFromProject(Long projectId, Long userId) {
        log.info("Removing user {} from project {}", userId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        User user = userService.findUserEntityById(userId);

        // Can't remove the project lead
        if (project.getLead().getId().equals(userId)) {
            throw new BadRequestException("Cannot remove project lead from members");
        }

        project.getMembers().remove(user);
        projectRepository.save(project);
        log.info("User removed from project successfully");
    }

    @Transactional
    public void archiveProject(Long id) {
        log.info("Archiving project: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        project.setArchived(true);
        projectRepository.save(project);
        log.info("Project archived successfully: {}", id);
    }

    @Transactional
    public void unarchiveProject(Long id) {
        log.info("Unarchiving project: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        project.setArchived(false);
        projectRepository.save(project);
        log.info("Project unarchived successfully: {}", id);
    }

    @Transactional
    public void deleteProject(Long id) {
        log.info("Deleting project: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        projectRepository.delete(project);
        log.info("Project deleted successfully: {}", id);
    }

    /**
     * Find project entity by ID (for internal service use)
     * This method is used by other services to get the Project entity
     *
     * @param id Project ID
     * @return Project entity
     * @throws ResourceNotFoundException if project not found
     */
    public Project findProjectEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    /**
     * Find project entity by key (for internal service use)
     *
     * @param key Project key
     * @return Project entity
     * @throws ResourceNotFoundException if project not found
     */
    public Project findProjectEntityByKey(String key) {
        return projectRepository.findByKey(key.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "key", key));
    }

    /**
     * Check if user is a member of the project
     *
     * @param projectId Project ID
     * @param userId User ID
     * @return true if user is member, false otherwise
     */
    public boolean isUserMemberOfProject(Long projectId, Long userId) {
        Project project = findProjectEntityById(projectId);
        return project.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
    }

    /**
     * Check if user is the project lead
     *
     * @param projectId Project ID
     * @param userId User ID
     * @return true if user is the lead, false otherwise
     */
    public boolean isUserProjectLead(Long projectId, Long userId) {
        Project project = findProjectEntityById(projectId);
        return project.getLead() != null && project.getLead().getId().equals(userId);
    }
}