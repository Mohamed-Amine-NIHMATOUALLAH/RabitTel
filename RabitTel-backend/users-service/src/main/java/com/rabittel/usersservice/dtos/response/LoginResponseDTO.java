package com.rabittel.usersservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {


    private String accessToken;


    private String tokenType;


    private boolean firstLogin;


    private UserResponseDTO user;

}