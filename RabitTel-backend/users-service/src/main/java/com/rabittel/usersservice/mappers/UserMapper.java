package com.rabittel.usersservice.mappers;

import com.rabittel.usersservice.dtos.request.AdminCreateUserRequestDTO;
import com.rabittel.usersservice.dtos.request.UserUpdateRequestDTO;
import com.rabittel.usersservice.dtos.response.UserResponseDTO;
import com.rabittel.usersservice.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)

    @Mapping(target = "active", ignore = true)

    @Mapping(target = "firstLogin", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)

    User toEntity(AdminCreateUserRequestDTO dto);


    UserResponseDTO toResponseDTO(User user);



    @Mapping(target = "id", ignore = true)

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)

    @Mapping(target = "role", ignore = true)

    @Mapping(target = "active", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)

    @Mapping(target = "firstLogin", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)

    void updateUserFromDTO(
            UserUpdateRequestDTO dto,
            @MappingTarget User user
    );

}