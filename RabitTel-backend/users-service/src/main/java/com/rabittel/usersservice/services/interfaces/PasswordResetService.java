package com.rabittel.usersservice.services.interfaces;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PasswordResetService {

    void forgotPassword(String email);

    void resetPassword(UUID userId);
}