package com.rabittel.notificationservice.services.implementations;

import com.rabittel.notificationservice.entities.NotificationDelivery;
import com.rabittel.notificationservice.enums.NotificationStatus;
import com.rabittel.notificationservice.repositories.NotificationDeliveryRepository;
import com.rabittel.notificationservice.services.interfaces.InAppNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class InAppNotificationServiceImpl implements InAppNotificationService {

    private final NotificationDeliveryRepository deliveryRepository;

    @Override
    public void send(NotificationDelivery delivery) {

        try {

            delivery.setStatus(NotificationStatus.SENT);
            delivery.setSentAt(LocalDateTime.now());
            delivery.setRead(false);

            deliveryRepository.save(delivery);

            log.info(
                    "In-App notification stored successfully for recipient={}",
                    delivery.getRecipient()
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to store In-App notification for recipient={}",
                    delivery.getRecipient(),
                    ex
            );

            delivery.setStatus(NotificationStatus.FAILED);
            delivery.setErrorMessage(ex.getMessage());

            deliveryRepository.save(delivery);

            throw new RuntimeException(
                    "Unable to create In-App notification.",
                    ex
            );
        }
    }
}