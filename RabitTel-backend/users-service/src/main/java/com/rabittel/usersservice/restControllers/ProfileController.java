package com.rabittel.usersservice.restControllers;

import com.rabittel.usersservice.dtos.request.UserUpdateRequestDTO;
import com.rabittel.usersservice.dtos.response.UserResponseDTO;
import com.rabittel.usersservice.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Authenticated user profile management")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get authenticated user profile")
    public UserResponseDTO getMyProfile() {

        return userService.getMyProfile();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update authenticated user profile")
    public UserResponseDTO updateMyProfile(
            @Valid
            @RequestBody
            UserUpdateRequestDTO request
    ) {

        return userService.updateMyProfile(request);
    }

}