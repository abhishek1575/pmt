package com.project.pmt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="attachments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attachment extends AuditEntity{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="issue_id", nullable=false)
    private Issue issue;

    @Column(nullable=false, length=255)
    private String filename;

    @Column(nullable=false, length=500)
    private String url;

    @Column(nullable=false, length=100)
    private String contentType;

    @Column(nullable=false)
    private Long size;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="uploaded_by", nullable=false)
    private User uploadedBy;
}
