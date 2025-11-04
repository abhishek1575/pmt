package com.project.pmt.repository;

import com.project.pmt.entity.Issue;
import com.project.pmt.entity.Project;
import com.project.pmt.enums.IssueStatus;
import com.project.pmt.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    Optional<Issue> findByIssueKey(String key);

    List<Issue> findByProjectId(Long projectId);

    Page<Issue> findByProjectId(Long projectId, Pageable pageable);

    List<Issue> findByProjectIdAndStatus(Long projectId, IssueStatus status);

    List<Issue> findByAssigneeId(Long assigneeId);

    List<Issue> findByReporterId(Long reporterId);

    List<Issue> findBySprintId(Long sprintId);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.project.id = :projectId AND i.status = :status")
    Long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") IssueStatus status);

    @Query("SELECT MAX(CAST(SUBSTRING(i.issueKey, LENGTH(:projectKey) + 2) AS int)) " +
            "FROM Issue i WHERE i.project.key = :projectKey")
    Integer findMaxIssueNumber(@Param("projectKey") String projectKey);

    @Query("SELECT MAX(i.boardOrder) FROM Issue i WHERE i.project.id = :projectId AND i.status = :status")
    Integer findMaxBoardOrder(@Param("projectId") Long projectId, @Param("status") IssueStatus status);

    @Query("SELECT i FROM Issue i WHERE " +
            "(:projectId IS NULL OR i.project.id = :projectId) AND " +
            "(:assigneeId IS NULL OR i.assignee.id = :assigneeId) AND " +
            "(:reporterId IS NULL OR i.reporter.id = :reporterId) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:priority IS NULL OR i.priority = :priority) AND " +
            "(:sprintId IS NULL OR i.sprint.id = :sprintId) AND " +
            "(:search IS NULL OR " +
            "LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.issueKey) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Issue> searchIssues(
            @Param("projectId") Long projectId,
            @Param("assigneeId") Long assigneeId,
            @Param("reporterId") Long reporterId,
            @Param("status") IssueStatus status,
            @Param("priority") Priority priority,
            @Param("sprintId") Long sprintId,
            @Param("search") String search,
            Pageable pageable
    );
}
