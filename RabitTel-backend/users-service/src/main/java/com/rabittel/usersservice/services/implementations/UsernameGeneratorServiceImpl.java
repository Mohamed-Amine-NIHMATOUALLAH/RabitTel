package com.rabittel.usersservice.services.implementations;

import com.rabittel.usersservice.repositories.UserRepository;
import com.rabittel.usersservice.services.interfaces.UsernameGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsernameGeneratorServiceImpl implements UsernameGeneratorService {

    private final UserRepository userRepository;

    @Override
    public String generateUsername(String firstName, String lastName) {

        String baseUsername =
                (firstName.trim() + "-" + lastName.trim())
                        .toLowerCase()
                        .replace(" ", "-");

        String username = baseUsername;

        int counter = 1;

        while (userRepository.existsByUsername(username)) {

            username = baseUsername + counter;

            counter++;
        }

        return username;
    }
}