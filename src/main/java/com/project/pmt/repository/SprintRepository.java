package com.project.pmt.repository;

import com.project.pmt.entity.Sprint;
import com.project.pmt.enums.SprintState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByUserId(Long userId);

    List<Sprint> findByProjectIdAndState(String projectId, SprintState state);

    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId AND s.state = 'ACTIVE'")
    Optional<Sprint> findActiveSprintByProjectId(@Param("projectId") Long projectId);

    Long countByProjectId(Long projectId);
}
