package com.rabittel.notificationservice.services.interfaces;

import com.rabittel.notificationservice.dtos.request.NotificationRequestDTO;
import com.rabittel.notificationservice.entities.Notification;
import com.rabittel.notificationservice.entities.NotificationDelivery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public interface NotificationDispatcher {

    List<NotificationDelivery> dispatch(
            Notification notification,
            NotificationRequestDTO request
    );
}