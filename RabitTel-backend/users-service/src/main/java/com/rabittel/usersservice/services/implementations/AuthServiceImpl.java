package com.rabittel.usersservice.services.implementations;


import com.rabittel.usersservice.dtos.request.ChangePasswordRequestDTO;
import com.rabittel.usersservice.dtos.request.LoginRequestDTO;
import com.rabittel.usersservice.dtos.response.LoginResponseDTO;
import com.rabittel.usersservice.dtos.response.UserResponseDTO;

import com.rabittel.usersservice.entities.User;

import com.rabittel.usersservice.repositories.UserRepository;

import com.rabittel.usersservice.security.CustomUserDetails;
import com.rabittel.usersservice.security.JwtService;

import com.rabittel.usersservice.services.interfaces.AuthService;


import lombok.RequiredArgsConstructor;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;



@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {



    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private static final int LOCK_TIME_MINUTES = 15;



    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {



        User user = userRepository.findByEmail(request.getEmail())

                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );


        if (!user.isActive()) {
            throw new RuntimeException(
                    "Votre compte est désactivé. Contactez un administrateur."
            );
        }

        // Check if account locked

        if(user.getLockedUntil() != null &&
                user.getLockedUntil()
                        .isAfter(LocalDateTime.now())){


            throw new RuntimeException(
                    "Account temporarily locked. Try later"
            );
        }



        try {


            Authentication authentication =
                    authenticationManager.authenticate(

                            new UsernamePasswordAuthenticationToken(

                                    request.getEmail(),

                                    request.getPassword()
                            )
                    );



            // Login successful

            user.setFailedLoginAttempts(0);

            user.setLockedUntil(null);

            user.setLastLoginAt(LocalDateTime.now());


            userRepository.save(user);



            CustomUserDetails userDetails =
                    new CustomUserDetails(user);



            String token = jwtService.generateToken(user, userDetails);



            UserResponseDTO responseUser =
                    new UserResponseDTO(

                            user.getId(),

                            user.getUsername(),

                            user.getFirstName(),

                            user.getLastName(),

                            user.getEmail(),

                            user.getPhoneNumber(),

                            user.getRole(),

                            user.isActive(),

                            user.getCreatedAt(),

                            user.getLastLoginAt()

                    );



            return new LoginResponseDTO(
                    token,
                    "Bearer",
                    user.isFirstLogin(),
                    responseUser
            );



        }catch (BadCredentialsException e){


            handleFailedLogin(user);


            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

    }




    private void handleFailedLogin(User user){


        int attempts =
                user.getFailedLoginAttempts() + 1;



        user.setFailedLoginAttempts(attempts);



        if(attempts >= MAX_LOGIN_ATTEMPTS){


            user.setLockedUntil(
                    LocalDateTime.now()
                            .plusMinutes(LOCK_TIME_MINUTES)
            );

            user.setFailedLoginAttempts(0);
        }



        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO request) {


        // Get current authenticated user email
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        String email = authentication.getName();



        // Find user
        User user = userRepository.findByEmail(email)

                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );



        // Check old password
        if(!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash()
        )){

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }



        // Encode new password
        user.setPasswordHash(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );



        // First login completed
        user.setFirstLogin(false);



        // Reset security counters
        user.setFailedLoginAttempts(0);

        user.setLockedUntil(null);



        userRepository.save(user);
    }

}