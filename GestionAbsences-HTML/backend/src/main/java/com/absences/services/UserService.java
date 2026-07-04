package com.absences.services;

import com.absences.dto.Dtos.*;
import com.absences.entities.User;
import com.absences.enums.Role;
import com.absences.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(UserCreateRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        User user = User.builder()
                .nom(req.nom())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(Role.valueOf(req.role().toUpperCase()))
                .build();
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserUpdateRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setNom(req.nom());
        user.setEmail(req.email());
        user.setRole(Role.valueOf(req.role().toUpperCase()));
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
