package com.rabittel.usersservice.clients;

import com.rabittel.usersservice.clients.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for the notification-service.
 *
 * <p>{@code name} must match {@code spring.application.name} of the
 * notification-service so Eureka can resolve {@code lb://notification-service}.
 * The {@code url} fallback is used only in local development when Eureka is
 * not running ({@code spring.cloud.discovery.enabled=false}).</p>
 *
 * <p><b>Design contract:</b> every call site wraps invocations in a
 * try/catch so that a notification failure never breaks the primary
 * business operation.</p>
 */
@FeignClient(
        name = "notification-service",
        url  = "${notification.service.url:}",
        path = "/api/v1/notifications"
)
public interface NotificationClient {

    /**
     * Sends a notification via one or more channels.
     *
     * @param request the notification payload
     */
    @PostMapping
    void send(@RequestBody NotificationRequest request);
}
