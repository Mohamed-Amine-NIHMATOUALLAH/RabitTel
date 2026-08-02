package com.rabittel.usersservice.services.interfaces;

import com.rabittel.usersservice.dtos.request.AdminCreateUserRequestDTO;
import com.rabittel.usersservice.dtos.request.AdminUpdateUserRequestDTO;
import com.rabittel.usersservice.dtos.request.UserUpdateRequestDTO;
import com.rabittel.usersservice.dtos.response.UserResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // ===========================
    // Admin
    // ===========================

    UserResponseDTO createUser(AdminCreateUserRequestDTO request);

    UserResponseDTO updateUser(UUID id, AdminUpdateUserRequestDTO request);

    void activateUser(UUID id);

    void deactivateUser(UUID id);

    Page<UserResponseDTO> getAllUsers(
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    UserResponseDTO getUserById(UUID id);



    // ===========================
    // Connected User
    // ===========================

    UserResponseDTO getMyProfile();

    UserResponseDTO updateMyProfile(UserUpdateRequestDTO request);

}