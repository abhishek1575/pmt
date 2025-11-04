package com.project.pmt.service;

import com.project.pmt.dto.request.CreateLabelRequest;
import com.project.pmt.dto.request.UpdateLabelRequest;
import com.project.pmt.dto.response.LabelResponse;
import com.project.pmt.entity.Label;
import com.project.pmt.exceptions.DuplicateResourceException;
import com.project.pmt.exceptions.ResourceNotFoundException;
import com.project.pmt.mapper.LabelMapper;
import com.project.pmt.repository.LabelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Transactional
    public LabelResponse createLabel(CreateLabelRequest request) {
        log.info("Creating label: {}", request.getName());

        if (labelRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Label", "name");
        }

        Label label = new Label();
        label.setName(request.getName());
        label.setColor(request.getColor());
        label.setDescription(request.getDescription());

        label = labelRepository.save(label);
        log.info("Label created successfully: {}", label.getId());

        return labelMapper.toResponse(label);
    }

    public LabelResponse getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", id));
        return labelMapper.toResponse(label);
    }

    public List<LabelResponse> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<LabelResponse> searchLabels(String name) {
        return labelRepository.findByNameContainingIgnoreCase(name).stream()
                .map(labelMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LabelResponse updateLabel(Long id, UpdateLabelRequest request) {
        log.info("Updating label: {}", id);

        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", id));

        if (request.getName() != null) {
            if (labelRepository.existsByName(request.getName()) &&
                    !label.getName().equals(request.getName())) {
                throw new DuplicateResourceException("Label", "name");
            }
            label.setName(request.getName());
        }
        if (request.getColor() != null) {
            label.setColor(request.getColor());
        }
        if (request.getDescription() != null) {
            label.setDescription(request.getDescription());
        }

        label = labelRepository.save(label);
        log.info("Label updated successfully: {}", label.getId());

        return labelMapper.toResponse(label);
    }

    @Transactional
    public void deleteLabel(Long id) {
        log.info("Deleting label: {}", id);

        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", id));

        labelRepository.delete(label);
        log.info("Label deleted successfully: {}", id);
    }
}
