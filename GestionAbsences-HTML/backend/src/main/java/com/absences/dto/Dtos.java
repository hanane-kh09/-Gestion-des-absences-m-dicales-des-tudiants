package com.absences.dto;

import java.time.LocalDate;

public class Dtos {

    public record LoginRequest(String email, String password) {}

    public record RegisterRequest(String nom, String email, String password) {}

    public record LoginResponse(String token, String role, String nom, Long id) {}

    public record AbsenceRequest(
        LocalDate startDate,
        LocalDate endDate,
        String reason
    ) {}

    public record ValidateRequest(Long absenceId, String comment) {}

    public record UserCreateRequest(
        String nom, String email, String password, String role
    ) {}

    public record UserUpdateRequest(
        String nom, String email, String role
    ) {}

    public record MessageResponse(String message) {}
}
