package com.rabittel.notificationservice.services.implementations;

import com.rabittel.notificationservice.dtos.request.NotificationRequestDTO;
import com.rabittel.notificationservice.dtos.response.NotificationResponseDTO;
import com.rabittel.notificationservice.entities.Notification;
import com.rabittel.notificationservice.entities.NotificationDelivery;
import com.rabittel.notificationservice.exception.ResourceNotFoundException;
import com.rabittel.notificationservice.mappers.NotificationMapper;
import com.rabittel.notificationservice.repositories.NotificationDeliveryRepository;
import com.rabittel.notificationservice.repositories.NotificationRepository;
import com.rabittel.notificationservice.services.interfaces.NotificationDispatcher;
import com.rabittel.notificationservice.services.interfaces.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Primary business logic for notification lifecycle management.
 *
 * <p>Orchestrates:
 * <ol>
 *   <li>Persisting the {@link Notification} record.</li>
 *   <li>Delegating channel delivery to {@link NotificationDispatcher}.</li>
 *   <li>Persisting all {@link NotificationDelivery} records.</li>
 * </ol>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationDispatcher notificationDispatcher;

    /**
     * Creates a {@link Notification}, dispatches it across all requested channels,
     * and persists the resulting {@link NotificationDelivery} records.
     *
     * @param request validated notification request
     * @return the created notification with all delivery statuses
     */
    @Override
    public NotificationResponseDTO send(NotificationRequestDTO request) {

        log.info("Sending notification type={} to recipient={} via channels={}",
                request.getType(), request.getRecipient(), request.getChannels());

        Notification notification = notificationMapper.toEntity(request);
        notification = notificationRepository.save(notification);

        log.debug("Notification persisted with id={}", notification.getId());

        List<NotificationDelivery> deliveries = notificationDispatcher.dispatch(notification, request);

        deliveryRepository.saveAll(deliveries);
        notification.setDeliveries(deliveries);
        notification = notificationRepository.save(notification);

        log.info("Notification id={} dispatched — {} deliveries created",
                notification.getId(), deliveries.size());

        return notificationMapper.toResponseDTO(notification);
    }

    /**
     * Fetches a single notification by its ID.
     *
     * @param id the notification UUID
     * @return the notification response DTO
     * @throws ResourceNotFoundException if no notification exists with the given ID
     */
    @Override
    public NotificationResponseDTO getById(UUID id) {

        log.debug("Fetching notification id={}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + id));

        return notificationMapper.toResponseDTO(notification);
    }

    /**
     * Returns all notifications in descending creation order.
     *
     * @return list of all notification response DTOs
     */
    @Override
    public List<NotificationResponseDTO> getAll() {

        log.debug("Fetching all notifications");

        return notificationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(notificationMapper::toResponseDTO)
                .toList();
    }

    /**
     * Returns all notifications that have at least one delivery targeting
     * the specified recipient, ordered by most recent delivery first.
     *
     * @param recipient email address or username of the recipient
     * @return list of matching notification response DTOs
     */
    @Override
    public List<NotificationResponseDTO> getRecipientNotifications(String recipient) {

        log.debug("Fetching notifications for recipient={}", recipient);

        return deliveryRepository.findByRecipientOrderBySentAtDesc(recipient)
                .stream()
                .map(NotificationDelivery::getNotification)
                .distinct()
                .map(notificationMapper::toResponseDTO)
                .toList();
    }

    /**
     * Marks a specific {@link NotificationDelivery} as read.
     * Idempotent — if already read, does nothing.
     *
     * @param deliveryId the delivery UUID (not the notification UUID)
     * @throws ResourceNotFoundException if no delivery exists with the given ID
     */
    @Override
    public void markAsRead(UUID deliveryId) {

        log.debug("Marking delivery id={} as read", deliveryId);

        NotificationDelivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification delivery not found with id: " + deliveryId));

        if (Boolean.TRUE.equals(delivery.getRead())) {
            log.debug("Delivery id={} is already read — no-op", deliveryId);
            return;
        }

        delivery.setRead(true);
        delivery.setReadAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        log.info("Delivery id={} marked as read", deliveryId);
    }
}
