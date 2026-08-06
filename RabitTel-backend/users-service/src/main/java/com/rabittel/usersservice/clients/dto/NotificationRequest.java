package com.rabittel.usersservice.clients.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Mirrors {@code NotificationRequestDTO} from the notification-service.
 *
 * <p>Kept in a dedicated {@code clients.dto} package so it never
 * bleeds into the domain layer of the users-service.</p>
 */
@Getter
@Builder
public class NotificationRequest {

    /** Recipient e-mail address. */
    private final String recipient;

    /**
     * Notification type — must match a value of
     * {@code com.rabittel.notificationservice.enums.NotificationType}.
     */
    private final String type;

    /**
     * Channels to dispatch — each value must match
     * {@code com.rabittel.notificationservice.enums.NotificationChannel}.
     */
    private final List<String> channels;

    /** Short title used for IN_APP deliveries. */
    private final String title;

    /** Short plain-text body used for IN_APP deliveries. */
    private final String body;

    /** UUID of the related domain entity (e.g. the User id). */
    private final UUID resourceId;

    /** Type name of the related domain entity (e.g. "USER"). */
    private final String resourceType;

    /**
     * Template variables injected into the Thymeleaf HTML template.
     * Keys must match the variable names used in the template file.
     */
    @Builder.Default
    private final Map<String, Object> variables = Map.of();
}
