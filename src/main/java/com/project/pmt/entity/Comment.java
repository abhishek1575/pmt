package com.project.pmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="comments")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Comment extends AuditEntity{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "issue_id" , nullable = false)
    private Issue issue;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name= "user_id", nullable = false)
    private User user;

    @Column(columnDefinition="TEXT")
    private String body;

    @Column(nullable=false)
    private Boolean edited= false;

    private LocalDateTime editedAt;


}
