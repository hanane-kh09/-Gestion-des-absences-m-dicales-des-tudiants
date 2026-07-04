package com.absences.repositories;

import com.absences.entities.StatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StatusLogRepository extends JpaRepository<StatusLog, Long> {
    List<StatusLog> findByAbsenceIdOrderByChangedAtAsc(Long absenceId);
}
