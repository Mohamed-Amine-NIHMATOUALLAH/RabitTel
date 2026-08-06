package com.rabittel.notificationservice.repositories;

import com.rabittel.notificationservice.entities.Notification;
import com.rabittel.notificationservice.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID>,
        JpaSpecificationExecutor<Notification> {

    /** Returns all notifications ordered by creation date descending. */
    List<Notification> findAllByOrderByCreatedAtDesc();

    List<Notification> findByResourceType(String resourceType);

    List<Notification> findByResourceId(UUID resourceId);

    List<Notification> findByType(NotificationType type);
}
