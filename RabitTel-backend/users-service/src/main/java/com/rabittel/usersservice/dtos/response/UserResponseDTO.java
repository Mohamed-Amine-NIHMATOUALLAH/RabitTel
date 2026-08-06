package com.rabittel.usersservice.dtos.response;

import com.rabittel.usersservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private boolean isActive;
    private boolean firstLogin;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}