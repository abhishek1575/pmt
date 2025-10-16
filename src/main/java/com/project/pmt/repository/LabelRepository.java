package com.project.pmt.repository;

import com.project.pmt.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Label findByName(String name);
    Boolean existsByName(String name);
    List<Label> findByNameContainingIgnoreCase(String name);
}
