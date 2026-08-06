package com.rabittel.usersservice.services.implementations;

import com.rabittel.usersservice.clients.NotificationClient;
import com.rabittel.usersservice.clients.dto.NotificationRequest;
import com.rabittel.usersservice.dtos.request.AdminCreateUserRequestDTO;
import com.rabittel.usersservice.dtos.request.AdminUpdateUserRequestDTO;
import com.rabittel.usersservice.dtos.request.UserUpdateRequestDTO;
import com.rabittel.usersservice.dtos.response.UserResponseDTO;
import com.rabittel.usersservice.entities.User;
import com.rabittel.usersservice.enums.UserRole;
import com.rabittel.usersservice.exceptions.BusinessRuleException;
import com.rabittel.usersservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.usersservice.exceptions.ResourceNotFoundException;
import com.rabittel.usersservice.mappers.UserMapper;
import com.rabittel.usersservice.repositories.UserRepository;
import com.rabittel.usersservice.services.interfaces.CurrentUserService;
import com.rabittel.usersservice.services.interfaces.PasswordGeneratorService;
import com.rabittel.usersservice.services.interfaces.UserService;
import com.rabittel.usersservice.services.interfaces.UsernameGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository             userRepository;
    private final UserMapper                 userMapper;
    private final PasswordEncoder            passwordEncoder;
    private final UsernameGeneratorService   usernameGeneratorService;
    private final PasswordGeneratorService   passwordGeneratorService;
    private final CurrentUserService         currentUserService;
    private final NotificationClient         notificationClient;

    @Value("${app.login-url:http://localhost:3000/login}")
    private String loginUrl;

    @Value("${app.support-url:http://localhost:3000/support}")
    private String supportUrl;

    // ──────────────────────────────────────────────────────────────────────────
    // Create user — sends welcome email with temporary credentials
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public UserResponseDTO createUser(AdminCreateUserRequestDTO request) {

        validateUniqueEmail(request.getEmail());
        validateUniquePhoneNumber(request.getPhoneNumber());

        String username          = usernameGeneratorService.generateUsername(request.getFirstName(), request.getLastName());
        String temporaryPassword = passwordGeneratorService.generateTemporaryPassword();

        User user = userMapper.toEntity(request);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        user.initializeNewUser();

        User savedUser = userRepository.save(user);

        // ── Notification: welcome email with credentials ──────────────────────
        sendNotification(NotificationRequest.builder()
                .recipient(savedUser.getEmail())
                .type("CREATE_USER")
                .channels(List.of("EMAIL"))
                .title("Bienvenue sur RabitTel")
                .body("Votre compte a été créé. Consultez votre email pour vos identifiants.")
                .resourceId(savedUser.getId())
                .resourceType("USER")
                .variables(Map.of(
                        "firstName",        savedUser.getFirstName(),
                        "email",            savedUser.getEmail(),
                        "username",         username,
                        "temporaryPassword", temporaryPassword,
                        "loginUrl",         loginUrl
                ))
                .build(),
                "create-user notification to " + savedUser.getEmail()
        );

        return userMapper.toResponseDTO(savedUser);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Update user role/status — notify if activated or deactivated
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public UserResponseDTO updateUser(UUID id, AdminUpdateUserRequestDTO request) {

        User user         = getUserEntity(id);
        User currentAdmin = currentUserService.getCurrentUser();

        if (currentAdmin.getId().equals(user.getId())) {
            if (user.getRole() != request.getRole() || user.isActive() != request.getIsActive()) {
                throw new BusinessRuleException("You cannot change your own role or account status.");
            }
        }

        long activeAdminCount = userRepository.countByRoleAndIsActiveTrue(UserRole.ADMIN);

        if (user.getRole() == UserRole.ADMIN && request.getRole() != UserRole.ADMIN && activeAdminCount == 1) {
            throw new BusinessRuleException("The last active administrator cannot lose the ADMIN role.");
        }

        if (user.getRole() == UserRole.ADMIN && Boolean.FALSE.equals(request.getIsActive()) && activeAdminCount == 1) {
            throw new BusinessRuleException("The last active administrator cannot be deactivated.");
        }

        boolean wasActive   = user.isActive();
        boolean willBeActive = request.getIsActive();

        user.setRole(request.getRole());
        user.setActive(willBeActive);
        User updatedUser = userRepository.save(user);

        // ── Notify status change ──────────────────────────────────────────────
        if (wasActive && !willBeActive) {
            sendNotification(NotificationRequest.builder()
                    .recipient(updatedUser.getEmail())
                    .type("ACCOUNT_DEACTIVATED")
                    .channels(List.of("EMAIL"))
                    .title("Compte désactivé")
                    .body("Votre compte RabitTel a été désactivé.")
                    .resourceId(updatedUser.getId())
                    .resourceType("USER")
                    .variables(Map.of(
                            "firstName",   updatedUser.getFirstName(),
                            "email",       updatedUser.getEmail(),
                            "supportUrl",  supportUrl
                    ))
                    .build(),
                    "account-deactivated notification to " + updatedUser.getEmail()
            );
        } else if (!wasActive && willBeActive) {
            sendNotification(NotificationRequest.builder()
                    .recipient(updatedUser.getEmail())
                    .type("ACCOUNT_ACTIVATED")
                    .channels(List.of("EMAIL"))
                    .title("Compte activé")
                    .body("Votre compte RabitTel a été réactivé.")
                    .resourceId(updatedUser.getId())
                    .resourceType("USER")
                    .variables(Map.of(
                            "firstName",   updatedUser.getFirstName(),
                            "email",       updatedUser.getEmail(),
                            "loginUrl",    loginUrl
                    ))
                    .build(),
                    "account-activated notification to " + updatedUser.getEmail()
            );
        }

        return userMapper.toResponseDTO(updatedUser);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Activate user
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void activateUser(UUID id) {

        User user = getUserEntity(id);

        if (user.isActive()) {
            throw new BusinessRuleException("User is already active.");
        }

        user.setActive(true);
        userRepository.save(user);

        // ── Notification: account unlocked / reactivated ──────────────────────
        sendNotification(NotificationRequest.builder()
                .recipient(user.getEmail())
                .type("ACCOUNT_UNLOCKED")
                .channels(List.of("EMAIL"))
                .title("Compte activé")
                .body("Votre compte RabitTel a été réactivé par un administrateur.")
                .resourceId(user.getId())
                .resourceType("USER")
                .variables(Map.of(
                        "firstName",   user.getFirstName(),
                        "email",       user.getEmail(),
                        "unlockedAt",  formatted(LocalDateTime.now()),
                        "unlockedBy",  "Administrateur",
                        "loginUrl",    loginUrl
                ))
                .build(),
                "account-unlocked notification to " + user.getEmail()
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Deactivate user
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deactivateUser(UUID id) {

        User user         = getUserEntity(id);
        User currentAdmin = currentUserService.getCurrentUser();

        if (currentAdmin.getId().equals(user.getId())) {
            throw new BusinessRuleException("You cannot deactivate your own account.");
        }

        if (!user.isActive()) {
            throw new BusinessRuleException("User is already inactive.");
        }

        long activeAdminCount = userRepository.countByRoleAndIsActiveTrue(UserRole.ADMIN);
        if (user.getRole() == UserRole.ADMIN && activeAdminCount == 1) {
            throw new BusinessRuleException("The last active administrator cannot be deactivated.");
        }

        user.setActive(false);
        userRepository.save(user);

        // ── Notification: account deactivated ────────────────────────────────
        sendNotification(NotificationRequest.builder()
                .recipient(user.getEmail())
                .type("ACCOUNT_DEACTIVATED")
                .channels(List.of("EMAIL"))
                .title("Compte désactivé")
                .body("Votre compte RabitTel a été désactivé.")
                .resourceId(user.getId())
                .resourceType("USER")
                .variables(Map.of(
                        "firstName",  user.getFirstName(),
                        "email",      user.getEmail(),
                        "supportUrl", supportUrl
                ))
                .build(),
                "account-deactivated notification to " + user.getEmail()
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Read-only operations — no changes
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    public UserResponseDTO getUserById(UUID id) {
        return userMapper.toResponseDTO(getUserEntity(id));
    }

    @Override
    public UserResponseDTO getMyProfile() {
        return userMapper.toResponseDTO(currentUserService.getCurrentUser());
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable).map(userMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public UserResponseDTO updateMyProfile(UserUpdateRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        validateEmailForProfileUpdate(currentUser, request.getEmail());
        validatePhoneNumberForProfileUpdate(currentUser, request.getPhoneNumber());
        userMapper.updateUserFromDTO(request, currentUser);
        return userMapper.toResponseDTO(userRepository.save(currentUser));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Fires a notification via Feign.
     * Wrapped in try/catch: a notification failure NEVER fails the business operation.
     *
     * @param request     the notification payload
     * @param description short description for the log entry
     */
    private void sendNotification(NotificationRequest request, String description) {
        try {
            notificationClient.send(request);
            log.info("Notification sent — {}", description);
        } catch (Exception ex) {
            log.error("Notification failed (non-blocking) — {}: {}", description, ex.getMessage());
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("Email already exists.");
        }
    }

    private void validateUniquePhoneNumber(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ResourceAlreadyExistsException("Phone number already exists.");
        }
    }

    private User getUserEntity(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private void validateEmailForProfileUpdate(User currentUser, String newEmail) {
        if (currentUser.getEmail().equalsIgnoreCase(newEmail)) return;
        if (userRepository.existsByEmail(newEmail)) {
            throw new ResourceAlreadyExistsException("Email already exists.");
        }
    }

    private void validatePhoneNumberForProfileUpdate(User currentUser, String newPhoneNumber) {
        if (currentUser.getPhoneNumber().equals(newPhoneNumber)) return;
        if (userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new ResourceAlreadyExistsException("Phone number already exists.");
        }
    }

    private String formatted(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
