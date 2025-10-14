package com.project.pmt.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="labels")
public class Label extends AuditEntity{

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,unique=true, length=50)
    private String name;

    @Column(nullable=false, length=7)
    private String color;

    @Column(length=200)
    private String description;

    @ManyToMany(mappedBy="labels")
    private Set<Issue> issues = new HashSet<>();
}
