package com.absences.config;

import com.absences.entities.User;
import com.absences.enums.Role;
import com.absences.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin
        if (!userRepository.existsByEmail("admin@absences.ma")) {
            User admin = User.builder()
                    .nom("Administrateur")
                    .email("admin@absences.ma")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Admin créé: admin@absences.ma / admin123");
        }

        // Create default agent
        if (!userRepository.existsByEmail("agent@absences.ma")) {
            User agent = User.builder()
                    .nom("Agent Scolarité")
                    .email("agent@absences.ma")
                    .password(passwordEncoder.encode("agent123"))
                    .role(Role.AGENT)
                    .build();
            userRepository.save(agent);
            System.out.println("✅ Agent créé: agent@absences.ma / agent123");
        }

        // Create default student
        if (!userRepository.existsByEmail("etudiant@absences.ma")) {
            User student = User.builder()
                    .nom("Etudiant Test")
                    .email("etudiant@absences.ma")
                    .password(passwordEncoder.encode("etudiant123"))
                    .role(Role.ETUDIANT)
                    .build();
            userRepository.save(student);
            System.out.println("✅ Etudiant créé: etudiant@absences.ma / etudiant123");
        }
    }
}
