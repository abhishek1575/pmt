package com.project.pmt.entity;

import com.project.pmt.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length=50)
    private String username;

    @Column(nullable=false)
    private String passwordHash;

    @Column(nullable=false, unique=true, length=100)
    private String email;

    @Column(nullable=false, length=100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Role role;

    @Column(nullable=false)
    private Boolean isActive = true;

    private String avtarUrl;

    @Column(length=15)
    private String phoneNumber;

    @Column(length=100)
    private String department;
}
