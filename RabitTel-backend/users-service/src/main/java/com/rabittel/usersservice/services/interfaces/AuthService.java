package com.rabittel.usersservice.services.interfaces;

import com.rabittel.usersservice.dtos.request.ChangePasswordRequestDTO;
import com.rabittel.usersservice.dtos.request.LoginRequestDTO;
import com.rabittel.usersservice.dtos.response.LoginResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    void changePassword(ChangePasswordRequestDTO request);
}