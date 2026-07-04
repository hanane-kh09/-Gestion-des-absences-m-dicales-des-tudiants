package com.absences.controllers;

import com.absences.dto.Dtos.*;
import com.absences.entities.User;
import com.absences.services.AbsenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/absences")
@RequiredArgsConstructor
public class AbsenceController {

    private final AbsenceService absenceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        try {
            User user = (User) auth.getPrincipal();
            AbsenceRequest req = new AbsenceRequest(
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate),
                    reason
            );
            return ResponseEntity.ok(absenceService.createAbsence(req, user, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/mes-absences")
    public ResponseEntity<?> mesAbsences(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(absenceService.getStudentAbsences(user.getId()));
    }

    @GetMapping
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(absenceService.getAllAbsences());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(absenceService.getById(id));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<?> getLogs(@PathVariable Long id) {
        return ResponseEntity.ok(absenceService.getLogs(id));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody ValidateRequest req, Authentication auth) {
        try {
            User agent = (User) auth.getPrincipal();
            absenceService.validateAbsence(req.absenceId(), req.comment(), agent);
            return ResponseEntity.ok(new MessageResponse("Absence validée avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/refuse")
    public ResponseEntity<?> refuse(@RequestBody ValidateRequest req, Authentication auth) {
        try {
            User agent = (User) auth.getPrincipal();
            absenceService.refuseAbsence(req.absenceId(), req.comment(), agent);
            return ResponseEntity.ok(new MessageResponse("Absence refusée"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/encours")
    public ResponseEntity<?> encours(@RequestBody ValidateRequest req, Authentication auth) {
        try {
            User agent = (User) auth.getPrincipal();
            absenceService.setEnCours(req.absenceId(), agent);
            return ResponseEntity.ok(new MessageResponse("Statut mis à jour"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
