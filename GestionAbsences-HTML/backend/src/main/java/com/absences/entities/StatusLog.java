package com.absences.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "absence_status_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "absence_id", nullable = false)
    @JsonIgnore
    private Absence absence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    private String oldStatus;

    @Column(nullable = false)
    private String newStatus;

    private String comment;

    @CreationTimestamp
    private LocalDateTime changedAt;
}
