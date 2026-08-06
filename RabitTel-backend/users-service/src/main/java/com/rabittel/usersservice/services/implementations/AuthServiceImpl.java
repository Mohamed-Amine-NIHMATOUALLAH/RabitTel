package com.rabittel.usersservice.services.implementations;

import com.rabittel.usersservice.clients.NotificationClient;
import com.rabittel.usersservice.clients.dto.NotificationRequest;
import com.rabittel.usersservice.dtos.request.ChangePasswordRequestDTO;
import com.rabittel.usersservice.dtos.request.LoginRequestDTO;
import com.rabittel.usersservice.dtos.response.LoginResponseDTO;
import com.rabittel.usersservice.dtos.response.UserResponseDTO;
import com.rabittel.usersservice.entities.User;
import com.rabittel.usersservice.repositories.UserRepository;
import com.rabittel.usersservice.security.CustomUserDetails;
import com.rabittel.usersservice.security.JwtService;
import com.rabittel.usersservice.services.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES  = 15;

    private final AuthenticationManager authenticationManager;
    private final UserRepository        userRepository;
    private final JwtService            jwtService;
    private final PasswordEncoder       passwordEncoder;
    private final NotificationClient    notificationClient;

    @Value("${app.support-url:http://localhost:3000/support}")
    private String supportUrl;

    // ──────────────────────────────────────────────────────────────────────────
    // Login — unchanged business logic, notification added on account lock
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.isActive()) {
            throw new RuntimeException("Votre compte est désactivé. Contactez un administrateur.");
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account temporarily locked. Try later");
        }

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Login successful — reset counters
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            CustomUserDetails userDetails = new CustomUserDetails(user);
            String token = jwtService.generateToken(user, userDetails);

            UserResponseDTO responseUser = new UserResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getRole(),
                    user.isActive(),
                    user.isFirstLogin(),
                    user.getCreatedAt(),
                    user.getLastLoginAt()
            );

            return new LoginResponseDTO(token, "Bearer", user.isFirstLogin(), responseUser);

        } catch (BadCredentialsException e) {

            handleFailedLogin(user);
            throw new RuntimeException("Invalid email or password");
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Change password — sends confirmation email
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    public void changePassword(ChangePasswordRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setFirstLogin(false);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        // ── Notification: password changed confirmation ───────────────────────
        sendNotification(NotificationRequest.builder()
                .recipient(user.getEmail())
                .type("CHANGE_PASSWORD")
                .channels(List.of("EMAIL"))
                .title("Mot de passe modifié")
                .body("Votre mot de passe RabitTel a été modifié avec succès.")
                .resourceId(user.getId())
                .resourceType("USER")
                .variables(Map.of(
                        "firstName",  user.getFirstName(),
                        "email",      user.getEmail(),
                        "changedAt",  formatted(LocalDateTime.now()),
                        "ipAddress",  "N/A",
                        "supportUrl", supportUrl
                ))
                .build(),
                "change-password notification to " + user.getEmail()
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private — failed login handler
    // Sends ACCOUNT_LOCKED notification when the threshold is reached
    // ──────────────────────────────────────────────────────────────────────────

    private void handleFailedLogin(User user) {

        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        boolean justLocked = attempts >= MAX_LOGIN_ATTEMPTS;

        if (justLocked) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES));
            user.setFailedLoginAttempts(0);
        }

        userRepository.save(user);

        // ── Notification: account locked ─────────────────────────────────────
        if (justLocked) {
            sendNotification(NotificationRequest.builder()
                    .recipient(user.getEmail())
                    .type("ACCOUNT_LOCKED")
                    .channels(List.of("EMAIL"))
                    .title("Compte temporairement verrouillé")
                    .body("Votre compte a été verrouillé suite à " + MAX_LOGIN_ATTEMPTS + " tentatives échouées.")
                    .resourceId(user.getId())
                    .resourceType("USER")
                    .variables(Map.of(
                            "firstName", user.getFirstName(),
                            "email",     user.getEmail(),
                            "lockedAt",  formatted(LocalDateTime.now()),
                            "reason",    MAX_LOGIN_ATTEMPTS + " tentatives de connexion échouées",
                            "supportUrl", supportUrl
                    ))
                    .build(),
                    "account-locked notification to " + user.getEmail()
            );
        }
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

    private String formatted(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
