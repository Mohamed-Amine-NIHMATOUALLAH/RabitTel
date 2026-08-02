package com.rabittel.usersservice.dtos.request;

import com.rabittel.usersservice.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateUserRequestDTO {

    @NotNull(message = "Role is required")
    private UserRole role;

    @NotNull(message = "User status is required")
    private Boolean isActive;
}