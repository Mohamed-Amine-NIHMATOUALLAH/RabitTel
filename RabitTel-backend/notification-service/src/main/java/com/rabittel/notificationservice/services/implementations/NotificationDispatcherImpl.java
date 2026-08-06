package com.rabittel.notificationservice.services.implementations;

import com.rabittel.notificationservice.dtos.request.EmailNotificationRequestDTO;
import com.rabittel.notificationservice.dtos.request.NotificationRequestDTO;
import com.rabittel.notificationservice.entities.Notification;
import com.rabittel.notificationservice.entities.NotificationDelivery;
import com.rabittel.notificationservice.enums.NotificationChannel;
import com.rabittel.notificationservice.enums.NotificationStatus;
import com.rabittel.notificationservice.services.interfaces.EmailService;
import com.rabittel.notificationservice.services.interfaces.InAppNotificationService;
import com.rabittel.notificationservice.services.interfaces.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Routes a notification to every requested channel and collects
 * {@link NotificationDelivery} records regardless of individual channel success.
 *
 * <p>Design contract:
 * <ul>
 *   <li>One {@link NotificationDelivery} is created per channel.</li>
 *   <li>A channel failure does NOT abort other channels.</li>
 *   <li>Adding a new channel requires adding exactly ONE service implementation
 *       and ONE case here — no other class needs to change.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcherImpl implements NotificationDispatcher {

    private final EmailService emailService;
    private final InAppNotificationService inAppNotificationService;

    /*
     * Future channel services — inject and add a case below.
     * private final SmsService smsService;
     * private final WhatsappService whatsappService;
     * private final TeamsService teamsService;
     */

    @Override
    public List<NotificationDelivery> dispatch(
            Notification notification,
            NotificationRequestDTO request
    ) {
        List<NotificationDelivery> deliveries = new ArrayList<>();

        if (request == null || request.getChannels() == null || request.getChannels().isEmpty()) {
            log.warn("Dispatch called with no channels for notification id={}", notification.getId());
            return deliveries;
        }

        for (NotificationChannel channel : request.getChannels()) {

            log.debug("Dispatching channel={} for recipient={}", channel, request.getRecipient());

            NotificationDelivery delivery = buildPendingDelivery(notification, channel, request);

            try {
                dispatchToChannel(channel, delivery, request);

                delivery.setStatus(NotificationStatus.SENT);
                delivery.setSentAt(LocalDateTime.now());

                log.info("Channel={} dispatched successfully to recipient={}",
                        channel, request.getRecipient());

            } catch (Exception ex) {

                log.error("Channel={} failed for recipient={}: {}",
                        channel, request.getRecipient(), ex.getMessage(), ex);

                delivery.setStatus(NotificationStatus.FAILED);
                delivery.setErrorMessage(truncate(ex.getMessage(), 1000));
            }

            deliveries.add(delivery);
        }

        return deliveries;
    }

    // ----------------------------------------------------------------
    // Private — per-channel routing
    // ----------------------------------------------------------------

    /**
     * Delegates to the correct channel service.
     * Each case is responsible for populating {@code delivery.subject} and
     * {@code delivery.body} so the record is always complete.
     */
    private void dispatchToChannel(
            NotificationChannel channel,
            NotificationDelivery delivery,
            NotificationRequestDTO request
    ) {
        switch (channel) {

            case EMAIL -> {

                EmailNotificationRequestDTO emailRequest = EmailNotificationRequestDTO.builder()
                        .recipient(request.getRecipient())
                        .type(request.getType())
                        .variables(request.getVariables())
                        .build();

                /*
                 * EmailService generates the HTML body internally via TemplateService.
                 * We store the generated subject here so the delivery record is descriptive.
                 * The body column stores a placeholder — full HTML is in the email itself.
                 */
                emailService.sendEmail(emailRequest);

                delivery.setSubject(emailRequest.getSubject() != null
                        ? emailRequest.getSubject()
                        : request.getType().name());
                delivery.setBody("Email delivered via SMTP.");
            }

            case IN_APP -> {

                /*
                 * For IN_APP, subject and body come directly from the request.
                 * The InAppNotificationService updates the delivery status and persists it.
                 */
                delivery.setSubject(request.getTitle() != null ? request.getTitle() : request.getType().name());
                delivery.setBody(request.getBody() != null ? request.getBody() : "");

                inAppNotificationService.send(delivery);
            }

            case SMS -> {
                /*
                 * EXTENSION POINT — inject SmsService and implement here.
                 * Example:
                 *   smsService.send(SmsNotificationRequestDTO.from(request));
                 *   delivery.setBody("SMS sent.");
                 */
                delivery.setBody("SMS channel not yet implemented.");
                throw new UnsupportedOperationException("SMS channel is not yet implemented.");
            }

            case WHATSAPP -> {
                /*
                 * EXTENSION POINT — inject WhatsappService and implement here.
                 */
                delivery.setBody("WhatsApp channel not yet implemented.");
                throw new UnsupportedOperationException("WhatsApp channel is not yet implemented.");
            }

            case MICROSOFT_TEAMS -> {
                /*
                 * EXTENSION POINT — inject TeamsService and implement here.
                 */
                delivery.setBody("Microsoft Teams channel not yet implemented.");
                throw new UnsupportedOperationException("Microsoft Teams channel is not yet implemented.");
            }

            default -> throw new IllegalArgumentException("Unknown channel: " + channel);
        }
    }

    /**
     * Builds a {@link NotificationDelivery} in {@code PENDING} state.
     * Body defaults to empty string to satisfy the {@code NOT NULL} DB constraint.
     */
    private NotificationDelivery buildPendingDelivery(
            Notification notification,
            NotificationChannel channel,
            NotificationRequestDTO request
    ) {
        return NotificationDelivery.builder()
                .notification(notification)
                .channel(channel)
                .recipient(request.getRecipient())
                .body("")
                .status(NotificationStatus.PENDING)
                .build();
    }

    /** Prevents oversized error messages from breaking the VARCHAR(1000) column. */
    private String truncate(String message, int maxLength) {
        if (message == null) return null;
        return message.length() <= maxLength ? message : message.substring(0, maxLength);
    }
}
