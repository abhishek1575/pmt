package com.project.pmt.service;

import com.project.pmt.dto.request.CreateSprintRequest;
import com.project.pmt.dto.request.UpdateSprintRequest;
import com.project.pmt.dto.response.SprintResponse;
import com.project.pmt.entity.Project;
import com.project.pmt.entity.Sprint;
import com.project.pmt.enums.SprintState;
import com.project.pmt.exceptions.BadRequestException;
import com.project.pmt.exceptions.ResourceNotFoundException;
import com.project.pmt.mapper.SprintMapper;
import com.project.pmt.repository.SprintRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectService projectService;
    private final SprintMapper sprintMapper;

    @Transactional
    public SprintResponse createSprint(CreateSprintRequest request) {
        log.info("Creating sprint for project: {}", request.getProjectId());

        Project project = projectService.findProjectEntityById(request.getProjectId());

        Sprint sprint = new Sprint();
        sprint.setProject(project);
        sprint.setName(request.getName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setState(request.getState() != null ? request.getState() : SprintState.PLANNED);

        sprint = sprintRepository.save(sprint);
        log.info("Sprint created successfully: {}", sprint.getId());

        return sprintMapper.toResponse(sprint);
    }

    public SprintResponse getSprintById(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", "id", id));
        return sprintMapper.toResponse(sprint);
    }

    public List<SprintResponse> getSprintsByProject(Long projectId) {
        return sprintRepository.findByProjectId(projectId).stream()
                .map(sprintMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SprintResponse getActiveSprint(Long projectId) {
        Sprint sprint = sprintRepository.findActiveSprintByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("No active sprint found for project"));
        return sprintMapper.toResponse(sprint);
    }

    @Transactional
    public SprintResponse updateSprint(Long id, UpdateSprintRequest request) {
        log.info("Updating sprint: {}", id);

        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", "id", id));

        if (request.getName() != null) {
            sprint.setName(request.getName());
        }
        if (request.getGoal() != null) {
            sprint.setGoal(request.getGoal());
        }
        if (request.getStartDate() != null) {
            sprint.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            sprint.setEndDate(request.getEndDate());
        }
        if (request.getState() != null) {
            sprint.setState(request.getState());
        }

        sprint = sprintRepository.save(sprint);
        log.info("Sprint updated successfully: {}", sprint.getId());

        return sprintMapper.toResponse(sprint);
    }

    @Transactional
    public SprintResponse startSprint(Long id) {
        log.info("Starting sprint: {}", id);

        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", "id", id));

        // Check if there's already an active sprint
        sprintRepository.findActiveSprintByProjectId(sprint.getProject().getId())
                .ifPresent(activeSprint -> {
                    throw new BadRequestException("There is already an active sprint for this project");
                });

        sprint.setState(SprintState.ACTIVE);
        sprint = sprintRepository.save(sprint);
        log.info("Sprint started successfully: {}", sprint.getId());

        return sprintMapper.toResponse(sprint);
    }

    @Transactional
    public SprintResponse completeSprint(Long id) {
        log.info("Completing sprint: {}", id);

        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", "id", id));

        sprint.setState(SprintState.COMPLETED);
        sprint = sprintRepository.save(sprint);
        log.info("Sprint completed successfully: {}", sprint.getId());

        return sprintMapper.toResponse(sprint);
    }

    @Transactional
    public void deleteSprint(Long id) {
        log.info("Deleting sprint: {}", id);

        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", "id", id));

        if (sprint.getState() == SprintState.ACTIVE) {
            throw new BadRequestException("Cannot delete an active sprint");
        }

        sprintRepository.delete(sprint);
        log.info("Sprint deleted successfully: {}", id);
    }
}