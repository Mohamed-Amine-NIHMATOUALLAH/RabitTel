package com.rabittel.usersservice.restControllers;

import com.rabittel.usersservice.dtos.request.ChangePasswordRequestDTO;
import com.rabittel.usersservice.dtos.request.LoginRequestDTO;
import com.rabittel.usersservice.dtos.response.LoginResponseDTO;
import com.rabittel.usersservice.services.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Authenticate user")
    public LoginResponseDTO login(
            @Valid
            @RequestBody
            LoginRequestDTO request
    ) {
        return authService.login(request);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Change user password")
    public void changePassword(
            @Valid
            @RequestBody
            ChangePasswordRequestDTO request
    ) {
        authService.changePassword(request);
    }

}