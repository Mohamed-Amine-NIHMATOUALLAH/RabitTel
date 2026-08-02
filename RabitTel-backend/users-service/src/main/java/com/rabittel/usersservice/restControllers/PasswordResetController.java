package com.rabittel.usersservice.restControllers;

import com.rabittel.usersservice.dtos.request.ResetPasswordRequestDTO;
import com.rabittel.usersservice.services.interfaces.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "Password reset management")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Forgot password")
    public void forgotPassword(
            @Valid
            @RequestBody
            ResetPasswordRequestDTO request
    ) {

        passwordResetService.forgotPassword(
                request.getEmail()
        );
    }

    @PatchMapping("/reset/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reset user password")
    public void resetPassword(
            @PathVariable
            java.util.UUID userId
    ) {

        passwordResetService.resetPassword(userId);

    }

}