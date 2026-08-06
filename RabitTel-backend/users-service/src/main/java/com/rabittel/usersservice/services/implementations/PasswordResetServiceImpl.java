package com.rabittel.usersservice.services.implementations;

import com.rabittel.usersservice.clients.NotificationClient;
import com.rabittel.usersservice.clients.dto.NotificationRequest;
import com.rabittel.usersservice.entities.User;
import com.rabittel.usersservice.exceptions.BusinessRuleException;
import com.rabittel.usersservice.exceptions.ResourceNotFoundException;
import com.rabittel.usersservice.repositories.UserRepository;
import com.rabittel.usersservice.services.interfaces.PasswordGeneratorService;
import com.rabittel.usersservice.services.interfaces.PasswordResetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository           userRepository;
    private final PasswordEncoder          passwordEncoder;
    private final PasswordGeneratorService passwordGeneratorService;
    private final NotificationClient       notificationClient;

    @Value("${app.login-url:http://localhost:3000/login}")
    private String loginUrl;

    // ──────────────────────────────────────────────────────────────────────────
    // Forgot password — user requests a reset via email
    // The admin will then call resetPassword(userId).
    // We notify the user that his request has been received.
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!user.isActive()) {
            throw new BusinessRuleException("User account is inactive.");
        }

        // Notify user: reset request acknowledged
        // (The admin handles the actual reset via resetPassword(userId))
        sendNotification(NotificationRequest.builder()
                .recipient(user.getEmail())
                .type("RESET_PASSWORD")
                .channels(List.of("EMAIL"))
                .title("Réinitialisation de mot de passe")
                .body("Votre demande de réinitialisation a été reçue. Un administrateur traitera votre demande.")
                .resourceId(user.getId())
                .resourceType("USER")
                .variables(Map.of(
                        "firstName",     user.getFirstName(),
                        "email",         user.getEmail(),
                        "resetToken",    "En attente de traitement par l'administrateur",
                        "expiryMinutes", "N/A"
                ))
                .build(),
                "forgot-password notification to " + user.getEmail()
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Reset password — admin generates a new temporary password
    // and sends it to the user via email
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    public void resetPassword(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Generate new temporary password
        String temporaryPassword = passwordGeneratorService.generateTemporaryPassword();

        user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        user.setFirstLogin(true);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        userRepository.save(user);

        // ── Notification: new temporary password by email ─────────────────────
        sendNotification(NotificationRequest.builder()
                .recipient(user.getEmail())
                .type("RESET_PASSWORD")
                .channels(List.of("EMAIL"))
                .title("Votre nouveau mot de passe temporaire")
                .body("Votre mot de passe a été réinitialisé. Connectez-vous avec le mot de passe temporaire ci-dessous.")
                .resourceId(user.getId())
                .resourceType("USER")
                .variables(Map.of(
                        "firstName",        user.getFirstName(),
                        "email",            user.getEmail(),
                        "resetToken",       temporaryPassword,
                        "expiryMinutes",    "Lors de votre prochaine connexion",
                        "resetUrl",         loginUrl
                ))
                .build(),
                "reset-password notification to " + user.getEmail()
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private helper
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Fires a notification via Feign.
     * Notification failure NEVER blocks the main operation.
     */
    private void sendNotification(NotificationRequest request, String description) {
        try {
            notificationClient.send(request);
            log.info("Notification sent — {}", description);
        } catch (Exception ex) {
            log.error("Notification failed (non-blocking) — {}: {}", description, ex.getMessage());
        }
    }
}
