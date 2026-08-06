package com.rabittel.notificationservice.entities;

import com.rabittel.notificationservice.enums.NotificationChannel;
import com.rabittel.notificationservice.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_deliveries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Parent Notification
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    /**
     * EMAIL
     * SMS
     * IN_APP
     * WHATSAPP
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    /**
     * Email
     * Phone
     * Username
     */
    @Column(nullable = false)
    private String recipient;

    /**
     * Email Subject
     */
    @Column(length = 255)
    private String subject;

    /**
     * Final generated content
     */
    @Lob
    @Column(nullable = false)
    private String body;

    /**
     * PENDING
     * SENT
     * FAILED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    /**
     * Sending date
     */
    private LocalDateTime sentAt;

    /**
     * Error if failed
     */
    @Column(length = 1000)
    private String errorMessage;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private Boolean read = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    public void prePersist() {

        if (status == null) {
            status = NotificationStatus.PENDING;
        }

    }

}