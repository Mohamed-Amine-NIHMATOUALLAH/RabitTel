package com.rabittel.usersservice.services.interfaces;

import com.rabittel.usersservice.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface CurrentUserService {

    User getCurrentUser();

}