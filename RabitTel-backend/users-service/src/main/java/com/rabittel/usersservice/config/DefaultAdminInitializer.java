package com.rabittel.usersservice.config;

import com.rabittel.usersservice.entities.User;
import com.rabittel.usersservice.enums.UserRole;
import com.rabittel.usersservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.existsByEmail("admin@gmail.com")) {
            return;
        }

        User admin = new User();

        admin.setUsername("admin");

        admin.setFirstName("System");

        admin.setLastName("Administrator");

        admin.setEmail("admin@gmail.com");

        admin.setPhoneNumber("0612345678");

        admin.setPasswordHash(
                passwordEncoder.encode("Admin@2026")
        );

        admin.setRole(UserRole.ADMIN);

        admin.initializeNewUser();

        admin.setFirstLogin(false);

        userRepository.save(admin);

        System.out.println("====================================");
        System.out.println(" Default administrator created");
        System.out.println(" Email: admin@gmail.com");
        System.out.println(" Password: Admin@2026");
        System.out.println("====================================");

    }

}