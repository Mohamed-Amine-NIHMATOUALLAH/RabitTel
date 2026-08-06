package com.rabittel.notificationservice.dtos.response;

import com.rabittel.notificationservice.enums.NotificationChannel;
import com.rabittel.notificationservice.enums.NotificationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDeliveryResponseDTO {

    private UUID id;

    private NotificationChannel channel;

    private String recipient;

    private String subject;

    private String body;

    private NotificationStatus status;

    private boolean read;

    private LocalDateTime readAt;

    private LocalDateTime sentAt;

    private String errorMessage;

}