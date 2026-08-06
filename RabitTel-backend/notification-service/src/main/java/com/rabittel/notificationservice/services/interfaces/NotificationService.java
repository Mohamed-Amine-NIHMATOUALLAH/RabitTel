package com.rabittel.notificationservice.services.interfaces;

import org.springframework.stereotype.Service;

import com.rabittel.notificationservice.dtos.request.NotificationRequestDTO;
import com.rabittel.notificationservice.dtos.response.NotificationResponseDTO;

import java.util.List;
import java.util.UUID;

@Service
public interface NotificationService {

    NotificationResponseDTO send(NotificationRequestDTO request);

    NotificationResponseDTO getById(UUID id);

    List<NotificationResponseDTO> getAll();

    List<NotificationResponseDTO> getRecipientNotifications(String recipient);

    void markAsRead(UUID id);

}