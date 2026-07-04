package com.absences.services;

import com.absences.dto.Dtos.*;
import com.absences.entities.User;
import com.absences.enums.Role;
import com.absences.repositories.UserRepository;
import com.absences.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Email introuvable"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, user.getRole().name(), user.getNom(), user.getId());
    }

    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        User user = User.builder()
                .nom(req.nom())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(Role.ETUDIANT)
                .build();
        userRepository.save(user);
    }
}
