package com.rabittel.notificationservice.dtos.request;

import com.rabittel.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationRequestDTO {


    @Email
    @NotBlank
    private String recipient;


    /**
     * Type of notification template
     */
    @NotNull
    private NotificationType type;


    /**
     * Optional custom subject
     */
    private String subject;


    @Builder.Default
    private Map<String, Object> variables = Map.of();

}