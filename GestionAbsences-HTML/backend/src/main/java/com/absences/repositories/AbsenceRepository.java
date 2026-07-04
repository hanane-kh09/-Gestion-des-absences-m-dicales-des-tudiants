package com.absences.repositories;

import com.absences.entities.Absence;
import com.absences.enums.AbsenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByStudentId(Long studentId);
    List<Absence> findByStatus(AbsenceStatus status);
    List<Absence> findAllByOrderByCreatedAtDesc();
    List<Absence> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
