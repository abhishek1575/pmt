package com.project.pmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="labels")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
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
