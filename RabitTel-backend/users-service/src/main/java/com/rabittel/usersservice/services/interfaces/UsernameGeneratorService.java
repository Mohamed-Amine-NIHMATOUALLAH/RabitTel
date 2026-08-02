package com.rabittel.usersservice.services.interfaces;

import org.springframework.stereotype.Service;

@Service
public interface UsernameGeneratorService {

    String generateUsername(String firstName, String lastName);

}