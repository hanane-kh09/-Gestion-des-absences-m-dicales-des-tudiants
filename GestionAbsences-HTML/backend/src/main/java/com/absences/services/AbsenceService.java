package com.absences.services;

import com.absences.dto.Dtos.AbsenceRequest;
import com.absences.entities.*;
import com.absences.enums.AbsenceStatus;
import com.absences.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AbsenceService {

    private final AbsenceRepository absenceRepository;
    private final DocumentRepository documentRepository;
    private final StatusLogRepository statusLogRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Absence createAbsence(AbsenceRequest req, User student, MultipartFile file) throws IOException {
        if (req.startDate().isAfter(req.endDate())) {
            throw new RuntimeException("La date de début doit être avant la date de fin");
        }
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Le justificatif est obligatoire");
        }

        Absence absence = Absence.builder()
                .student(student)
                .startDate(req.startDate())
                .endDate(req.endDate())
                .reason(req.reason())
                .status(AbsenceStatus.EN_ATTENTE)
                .build();
        absenceRepository.save(absence);

        // Save file
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), uploadPath.resolve(fileName));

        Document doc = Document.builder()
                .absence(absence)
                .filePath(fileName)
                .fileType(file.getContentType())
                .build();
        documentRepository.save(doc);

        // Initial log
        saveLog(absence, null, null, "EN_ATTENTE", "Demande créée", student);

        return absence;
    }

    public void validateAbsence(Long absenceId, String comment, User agent) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new RuntimeException("Le commentaire est obligatoire");
        }
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new RuntimeException("Absence introuvable"));
        String oldStatus = absence.getStatus().name();
        absence.setStatus(AbsenceStatus.ACCEPTEE);
        absence.setAgentComment(comment);
        absenceRepository.save(absence);
        saveLog(absence, agent, oldStatus, "ACCEPTEE", comment, agent);
    }

    public void refuseAbsence(Long absenceId, String comment, User agent) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new RuntimeException("Le commentaire est obligatoire");
        }
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new RuntimeException("Absence introuvable"));
        String oldStatus = absence.getStatus().name();
        absence.setStatus(AbsenceStatus.REFUSEE);
        absence.setAgentComment(comment);
        absenceRepository.save(absence);
        saveLog(absence, agent, oldStatus, "REFUSEE", comment, agent);
    }

    public void setEnCours(Long absenceId, User agent) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new RuntimeException("Absence introuvable"));
        String oldStatus = absence.getStatus().name();
        absence.setStatus(AbsenceStatus.EN_COURS);
        absenceRepository.save(absence);
        saveLog(absence, agent, oldStatus, "EN_COURS", "Prise en charge", agent);
    }

    public List<StatusLog> getLogs(Long absenceId) {
        return statusLogRepository.findByAbsenceIdOrderByChangedAtAsc(absenceId);
    }

    public List<Absence> getAllAbsences() {
        return absenceRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Absence> getStudentAbsences(Long studentId) {
        return absenceRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    public Absence getById(Long id) {
        return absenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence introuvable"));
    }

    private void saveLog(Absence absence, User agent, String oldStatus,
                         String newStatus, String comment, User changedBy) {
        StatusLog log = StatusLog.builder()
                .absence(absence)
                .changedBy(changedBy)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .comment(comment)
                .build();
        statusLogRepository.save(log);
    }
}
