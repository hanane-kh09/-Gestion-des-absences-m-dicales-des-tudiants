package com.absences.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "absence_id", nullable = false)
    @JsonIgnore
    private Absence absence;

    @Column(nullable = false)
    private String filePath;

    private String fileType;

    @CreationTimestamp
    private LocalDateTime uploadedAt;
}
