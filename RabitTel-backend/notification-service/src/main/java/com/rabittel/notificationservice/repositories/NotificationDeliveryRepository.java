package com.rabittel.notificationservice.repositories;

import com.rabittel.notificationservice.entities.NotificationDelivery;
import com.rabittel.notificationservice.enums.NotificationChannel;
import com.rabittel.notificationservice.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface NotificationDeliveryRepository
        extends JpaRepository<NotificationDelivery, UUID>,
        JpaSpecificationExecutor<NotificationDelivery> {

    List<NotificationDelivery> findByRecipient(String recipient);

    List<NotificationDelivery> findByStatus(NotificationStatus status);

    List<NotificationDelivery> findByChannel(NotificationChannel channel);

    List<NotificationDelivery> findByNotificationId(UUID notificationId);

    long countByStatus(NotificationStatus status);

    List<NotificationDelivery>
    findByRecipientOrderBySentAtDesc(String recipient);

    List<NotificationDelivery>
    findByRecipientAndChannel(
            String recipient,
            NotificationChannel channel
    );

}