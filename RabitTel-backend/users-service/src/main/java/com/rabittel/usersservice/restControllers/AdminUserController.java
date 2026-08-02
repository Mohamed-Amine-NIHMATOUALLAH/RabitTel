package com.rabittel.usersservice.restControllers;

import com.rabittel.usersservice.dtos.request.AdminCreateUserRequestDTO;
import com.rabittel.usersservice.dtos.request.AdminUpdateUserRequestDTO;
import com.rabittel.usersservice.dtos.response.UserResponseDTO;
import com.rabittel.usersservice.services.interfaces.PasswordResetService;
import com.rabittel.usersservice.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Administrator user management")
public class AdminUserController {

    private final UserService userService;

    private final PasswordResetService passwordResetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user")
    public UserResponseDTO createUser(
            @Valid
            @RequestBody
            AdminCreateUserRequestDTO request
    ) {

        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user role and status")
    public UserResponseDTO updateUser(
            @PathVariable UUID id,
            @Valid
            @RequestBody
            AdminUpdateUserRequestDTO request
    ) {

        return userService.updateUser(id, request);
    }

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Activate user")
    public void activateUser(
            @PathVariable UUID id
    ) {

        userService.activateUser(id);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate user")
    public void deactivateUser(
            @PathVariable UUID id
    ) {

        userService.deactivateUser(id);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public UserResponseDTO getUserById(
            @PathVariable UUID id
    ) {

        return userService.getUserById(id);
    }

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    public Page<UserResponseDTO> getAllUsers(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String sortDirection

    ) {

        return userService.getAllUsers(
                page,
                size,
                sortBy,
                sortDirection
        );
    }

    @PatchMapping("/{id}/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reset user password")
    public void resetPassword(
            @PathVariable UUID id
    ) {

        passwordResetService.resetPassword(id);
    }

}