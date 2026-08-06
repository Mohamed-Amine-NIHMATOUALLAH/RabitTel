package com.rabittel.notificationservice.dtos.response;

import com.rabittel.notificationservice.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private UUID id;

    private NotificationType type;

    private String resourceType;

    private UUID resourceId;

    private LocalDateTime createdAt;

    private long deliveryCount;

    private List<NotificationDeliveryResponseDTO> deliveries;

}