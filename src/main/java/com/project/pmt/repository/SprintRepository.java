package com.project.pmt.repository;

import com.project.pmt.entity.Sprint;
import com.project.pmt.enums.SprintState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    /**
     * Find all sprints for a specific project
     * @param projectId Project ID
     * @return List of sprints
     */
    List<Sprint> findByProjectId(Long projectId);

    /**
     * Find sprints by project ID and state
     * @param projectId Project ID
     * @param state Sprint state
     * @return List of sprints
     */
    List<Sprint> findByProjectIdAndState(Long projectId, SprintState state);

    /**
     * Find active sprint for a project
     * @param projectId Project ID
     * @return Optional of Sprint
     */
    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId AND s.state = 'ACTIVE'")
    Optional<Sprint> findActiveSprintByProjectId(@Param("projectId") Long projectId);

    /**
     * Count sprints for a project
     * @param projectId Project ID
     * @return Count of sprints
     */
    Long countByProjectId(Long projectId);
}