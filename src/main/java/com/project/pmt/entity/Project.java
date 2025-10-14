package com.project.pmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project extends AuditEntity{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name;

    @Column(unique=true, nullable=false, length=20)
    private String key;

    @Column(length=1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lead_id")
    private User lead;

    private String iconUrl;

    @Column(nullable=false)
    private Boolean archived = false;

    @OneToMany(mappedBy="project", cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<Issue> issues = new HashSet<>();

    @OneToMany(mappedBy= "project", cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<Sprint> sprints = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name="project_members",
            joinColumns=@JoinColumn(name="project_id"),
            inverseJoinColumns=@JoinColumn(name="user_id")
    )
    private Set<User> members = new HashSet<>();
}
