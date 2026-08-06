package com.rabittel.notificationservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationTemplate {

    CREATE_USER(
            NotificationType.CREATE_USER,
            "Welcome to RabitTel",
            "create-user"
    ),

    RESET_PASSWORD(
            NotificationType.RESET_PASSWORD,
            "Reset your password",
            "reset-password"
    ),

    CHANGE_PASSWORD(
            NotificationType.CHANGE_PASSWORD,
            "Password changed",
            "change-password"
    ),

    ACCOUNT_LOCKED(
            NotificationType.ACCOUNT_LOCKED,
            "Account locked",
            "account-locked"
    ),

    ACCOUNT_UNLOCKED(
            NotificationType.ACCOUNT_UNLOCKED,
            "Account unlocked",
            "account-unlocked"
    ),

    ACCOUNT_ACTIVATED(
            NotificationType.ACCOUNT_ACTIVATED,
            "Account activated",
            "account-unlocked"   // reuses the account-unlocked template
    ),

    ACCOUNT_DEACTIVATED(
            NotificationType.ACCOUNT_DEACTIVATED,
            "Account deactivated",
            "account-locked"     // reuses the account-locked template
    );

    private final NotificationType type;

    private final String subject;

    private final String template;
}