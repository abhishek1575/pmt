package com.project.pmt.entity;

import com.project.pmt.enums.IssueStatus;
import com.project.pmt.enums.IssueType;
import com.project.pmt.enums.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="issues", indexes={
        @Index(name="index_issue_project", columnList= "project_id"),
        @Index(name="index_issue_assignee", columnList= "assignee_id"),
        @Index(name="index_issue_status", columnList= "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Issue extends AuditEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @Column(nullable=false, length=20)
    private String issueKey;

    @Column(nullable=false, length=200)
    private String title;

    @Column(columnDefinition="Text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IssueType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private IssueStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reporter_id", nullable=false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assignee_id", nullable=false)
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sprint_id")
    private Sprint sprint;

    private LocalDateTime resolvedAt;

    private LocalDateTime dueDate;

    private Integer estimatedHours;

    private Integer LoggedHours;

    @Column(nullable=false)
    private Integer boardOrder=0;

    @OneToMany(mappedBy= "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy= "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attachment> attachments = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name= "issue_labels",
            joinColumns = @JoinColumn(name="issue_id"),
            inverseJoinColumns = @JoinColumn(name="label_id")
    )
    private Set<Label> labels = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_issue_id")
    private Issue parentIssue;

    @OneToMany(mappedBy= "parentIssue")
    private Set<Issue> subIssues = new HashSet<>();
}
