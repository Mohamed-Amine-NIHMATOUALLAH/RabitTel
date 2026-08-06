package com.rabittel.notificationservice.dtos.request;

import com.rabittel.notificationservice.enums.NotificationChannel;
import com.rabittel.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {

    @NotBlank
    private String recipient;

    @NotNull
    private NotificationType type;

    @NotNull
    private List<NotificationChannel> channels;

    private String title;

    private String body;

    private UUID resourceId;

    private String resourceType;

    @Builder.Default
    private Map<String, Object> variables = Map.of();
}