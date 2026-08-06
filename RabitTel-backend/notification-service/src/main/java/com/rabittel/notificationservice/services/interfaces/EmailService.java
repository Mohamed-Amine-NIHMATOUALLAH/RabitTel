package com.rabittel.notificationservice.services.interfaces;

import com.rabittel.notificationservice.dtos.request.EmailNotificationRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendEmail(EmailNotificationRequestDTO request);

}