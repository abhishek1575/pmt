package com.project.pmt.entity;

import com.project.pmt.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="notifications")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Notification extends AuditEntity{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=50)
    private NotificationType type;

    @Column(nullable=false, length=500)
    private String message;

    @Column(length=500)
    private String link;

    @Column(nullable=false)
    private Boolean read = false;

    private LocalDateTime readAt;

    @ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn(name="issue_id")
    private Issue issue;


}
