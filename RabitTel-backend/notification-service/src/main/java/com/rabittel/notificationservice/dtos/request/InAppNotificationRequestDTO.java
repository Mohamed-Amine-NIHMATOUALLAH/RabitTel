package com.rabittel.notificationservice.dtos.request;

import com.rabittel.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InAppNotificationRequestDTO {

    @NotBlank
    private String recipient;

    @NotNull
    private NotificationType type;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    private UUID resourceId;

    private String resourceType;

}