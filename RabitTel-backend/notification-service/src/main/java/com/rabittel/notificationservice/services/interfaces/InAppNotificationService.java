package com.rabittel.notificationservice.services.interfaces;

import com.rabittel.notificationservice.dtos.request.InAppNotificationRequestDTO;
import com.rabittel.notificationservice.entities.NotificationDelivery;
import org.springframework.stereotype.Service;

@Service
public interface InAppNotificationService {

    void send(NotificationDelivery delivery);

}