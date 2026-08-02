package com.rabittel.usersservice.services.implementations;

import com.rabittel.usersservice.entities.User;
import com.rabittel.usersservice.exceptions.BusinessRuleException;
import com.rabittel.usersservice.exceptions.ResourceNotFoundException;
import com.rabittel.usersservice.repositories.UserRepository;
import com.rabittel.usersservice.services.interfaces.PasswordGeneratorService;
import com.rabittel.usersservice.services.interfaces.PasswordResetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGeneratorService passwordGeneratorService;

    @Override
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );

        if (!user.isActive()) {
            throw new BusinessRuleException(
                    "User account is inactive."
            );
        }

        // TODO
        // notification-service.notifyAdminForgotPassword(user);
    }

    @Override
    public void resetPassword(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );

        String temporaryPassword =
                passwordGeneratorService.generateTemporaryPassword();

        user.setPasswordHash(
                passwordEncoder.encode(temporaryPassword)
        );

        user.setFirstLogin(true);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        userRepository.save(user);

        // TODO
        // notification-service.sendTemporaryPassword(
        //      user.getEmail(),
        //      temporaryPassword
        // );
    }
}