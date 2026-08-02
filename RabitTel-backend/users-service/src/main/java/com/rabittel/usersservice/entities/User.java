package com.rabittel.usersservice.entities;

import com.rabittel.usersservice.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;


    @NotBlank
    @Column(nullable = false)
    private String firstName;


    @NotBlank
    @Column(nullable = false)
    private String lastName;


    @NotBlank
    @Column(nullable = false, unique = true)
    private String phoneNumber;


    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = false, length = 255)
    private String passwordHash;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;


    @Column(name = "is_active", nullable = false)
    private boolean isActive;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    private LocalDateTime lastLoginAt;


    @Column(nullable = false)
    private boolean firstLogin = true;


    // Security fields

    @Column(nullable = false)
    private int failedLoginAttempts = 0;


    private LocalDateTime lockedUntil;


    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }

    public void initializeNewUser() {
        this.firstLogin = true;
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.isActive = true;
    }
}