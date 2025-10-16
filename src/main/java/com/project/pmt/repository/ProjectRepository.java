package com.project.pmt.repository;

import com.project.pmt.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByKey(String key);

    Boolean existsByKey(String key);

    List<Project> findByArchivedFalse();

    @Query("SELECT p FROM Project p WHERE p.archived = false")
    List<Project> findActiveProjects(Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.id = :userId AND p.archived = false")
    List<Project> findProjectsByMemberId(@Param("userId") Long userId);

    @Query("SELECT p FROM Project p WHERE " +
            "p.archived = false AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.key) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> searchProjects(@Param("search") String search, Pageable pageable);

}
