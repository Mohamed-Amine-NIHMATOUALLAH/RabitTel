package com.rabittel.notificationservice.entities;

import com.rabittel.notificationservice.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * CREATE_USER
     * RESET_PASSWORD
     * ...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    /**
     * USER
     * LINE
     * CONTRACT
     */
    @Column(nullable = false, length = 50)
    private String resourceType;

    /**
     * UUID of related entity
     */
    @Column(nullable = false)
    private UUID resourceId;

    /**
     * Creation date
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * One Notification
     * →
     * Many Deliveries
     */
    @Builder.Default
    @OneToMany(
            mappedBy = "notification",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<NotificationDelivery> deliveries = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public void addDelivery(NotificationDelivery delivery) {

        deliveries.add(delivery);

        delivery.setNotification(this);
    }

    public void removeDelivery(NotificationDelivery delivery) {

        deliveries.remove(delivery);

        delivery.setNotification(null);
    }

}