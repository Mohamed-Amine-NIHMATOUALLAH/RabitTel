package com.rabittel.usersservice.dtos.request;

import com.rabittel.usersservice.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateUserRequestDTO {

    @NotBlank(message = "First name is required")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ\\s'-]{2,50}$",
            message = "Invalid first name"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ\\s'-]{2,50}$",
            message = "Invalid last name"
    )
    private String lastName;


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(\\+212|0)[5-7][0-9]{8}$",
            message = "Invalid Moroccan phone number"
    )
    private String phoneNumber;

    @NotNull(message = "Role is required")
    private UserRole role;
}