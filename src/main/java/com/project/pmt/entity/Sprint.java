package com.project.pmt.entity;

import com.project.pmt.enums.SprintState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name="sprints")
public class Sprint extends AuditEntity{

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @Column(nullable=false, length=100)
    private String name;

    @Column(length=500)
    private String goal;

    private LocalDate startDate;

    private  LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private SprintState state;

    @OneToMany(mappedBy="sprint")
    private Set<Issue> issues = new HashSet<>();
}
