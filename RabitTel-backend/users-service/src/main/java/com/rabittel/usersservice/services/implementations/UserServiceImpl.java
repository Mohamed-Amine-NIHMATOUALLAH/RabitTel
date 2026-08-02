package com.rabittel.usersservice.services.implementations;

import com.rabittel.usersservice.dtos.request.AdminCreateUserRequestDTO;
import com.rabittel.usersservice.dtos.request.AdminUpdateUserRequestDTO;
import com.rabittel.usersservice.dtos.request.UserUpdateRequestDTO;
import com.rabittel.usersservice.dtos.response.UserResponseDTO;
import com.rabittel.usersservice.entities.User;
import com.rabittel.usersservice.enums.UserRole;
import com.rabittel.usersservice.exceptions.BusinessRuleException;
import com.rabittel.usersservice.exceptions.ResourceAlreadyExistsException;
import com.rabittel.usersservice.exceptions.ResourceNotFoundException;
import com.rabittel.usersservice.mappers.UserMapper;
import com.rabittel.usersservice.repositories.UserRepository;
import com.rabittel.usersservice.services.interfaces.CurrentUserService;
import com.rabittel.usersservice.services.interfaces.PasswordGeneratorService;
import com.rabittel.usersservice.services.interfaces.UserService;
import com.rabittel.usersservice.services.interfaces.UsernameGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final UsernameGeneratorService usernameGeneratorService;

    private final PasswordGeneratorService passwordGeneratorService;

    private final CurrentUserService currentUserService;


    @Override
    @Transactional
    public UserResponseDTO createUser(AdminCreateUserRequestDTO request) {

        // 1. Check email uniqueness
        validateUniqueEmail(request.getEmail());

        // 2. Check phone uniqueness
        validateUniquePhoneNumber(request.getPhoneNumber());

        // 3. Generate username
        String username = usernameGeneratorService.generateUsername(
                request.getFirstName(),
                request.getLastName()
        );

        // 4. Generate temporary password
        String temporaryPassword =
                passwordGeneratorService.generateTemporaryPassword();

        // 5. Map DTO → Entity
        User user = userMapper.toEntity(request);

        // 6. Complete entity
        user.setUsername(username);

        user.setPasswordHash(
                passwordEncoder.encode(temporaryPassword)
        );

        user.initializeNewUser();

        // 7. Save
        User savedUser = userRepository.save(user);

        // 8. TODO
        // notification-service.sendCredentials(
        //      savedUser.getEmail(),
        //      username,
        //      temporaryPassword
        // );

        // 9. Response
        return userMapper.toResponseDTO(savedUser);
    }


    @Override
    @Transactional
    public UserResponseDTO updateUser(
            UUID id,
            AdminUpdateUserRequestDTO request
    ) {

        // Get user to update
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );

        // Get authenticated administrator
        User currentAdmin = currentUserService.getCurrentUser();

        // Rule 1:
        // An administrator cannot modify his own role or account status.
        if (currentAdmin.getId().equals(user.getId())) {

            if (user.getRole() != request.getRole()
                    || user.isActive() != request.getIsActive()) {

                throw new BusinessRuleException(
                        "You cannot change your own role or account status."
                );
            }
        }

        // Number of ACTIVE administrators
        long activeAdminCount =
                userRepository.countByRoleAndIsActiveTrue(UserRole.ADMIN);

        // Rule 2:
        // The last active administrator cannot lose the ADMIN role.
        if (user.getRole() == UserRole.ADMIN
                && request.getRole() != UserRole.ADMIN
                && activeAdminCount == 1) {

            throw new BusinessRuleException(
                    "The last active administrator cannot lose the ADMIN role."
            );
        }

        // Rule 3:
        // The last active administrator cannot be deactivated.
        if (user.getRole() == UserRole.ADMIN
                && Boolean.FALSE.equals(request.getIsActive())
                && activeAdminCount == 1) {

            throw new BusinessRuleException(
                    "The last active administrator cannot be deactivated."
            );
        }

        // Update
        user.setRole(request.getRole());
        user.setActive(request.getIsActive());

        User updatedUser = userRepository.save(user);

        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public UserResponseDTO getUserById(UUID id) {

        User user = getUserEntity(id);

        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO getMyProfile() {

        User currentUser = currentUserService.getCurrentUser();

        return userMapper.toResponseDTO(currentUser);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {

        User user = getUserEntity(id);

        User currentAdmin = currentUserService.getCurrentUser();

        // Admin cannot deactivate himself
        if (currentAdmin.getId().equals(user.getId())) {
            throw new BusinessRuleException(
                    "You cannot deactivate your own account."
            );
        }

        // Already inactive
        if (!user.isActive()) {
            throw new BusinessRuleException(
                    "User is already inactive."
            );
        }

        // Protect last active administrator
        long activeAdminCount =
                userRepository.countByRoleAndIsActiveTrue(UserRole.ADMIN);

        if (user.getRole() == UserRole.ADMIN
                && activeAdminCount == 1) {

            throw new BusinessRuleException(
                    "The last active administrator cannot be deactivated."
            );
        }

        user.setActive(false);

        userRepository.save(user);
    }


    @Override
    public Page<UserResponseDTO> getAllUsers(
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        Page<User> users =
                userRepository.findAll(pageable);

        return users.map(userMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public UserResponseDTO updateMyProfile(UserUpdateRequestDTO request) {

        User currentUser = currentUserService.getCurrentUser();

        validateEmailForProfileUpdate(
                currentUser,
                request.getEmail()
        );

        validatePhoneNumberForProfileUpdate(
                currentUser,
                request.getPhoneNumber()
        );

        userMapper.updateUserFromDTO(
                request,
                currentUser
        );

        User updatedUser =
                userRepository.save(currentUser);

        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public void activateUser(UUID id) {

        User user = getUserEntity(id);

        if (user.isActive()) {
            throw new BusinessRuleException(
                    "User is already active."
            );
        }

        user.setActive(true);

        userRepository.save(user);
    }


    private void validateUniqueEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException(
                    "Email already exists."
            );
        }
    }

    private void validateUniquePhoneNumber(String phoneNumber) {

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ResourceAlreadyExistsException(
                    "Phone number already exists."
            );
        }
    }

    private User getUserEntity(UUID id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        )
                );
    }

    private void validateEmailForProfileUpdate(
            User currentUser,
            String newEmail
    ) {

        if (currentUser.getEmail().equalsIgnoreCase(newEmail)) {
            return;
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new ResourceAlreadyExistsException(
                    "Email already exists."
            );
        }
    }

    private void validatePhoneNumberForProfileUpdate(
            User currentUser,
            String newPhoneNumber
    ) {

        if (currentUser.getPhoneNumber().equals(newPhoneNumber)) {
            return;
        }

        if (userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new ResourceAlreadyExistsException(
                    "Phone number already exists."
            );
        }
    }


}
