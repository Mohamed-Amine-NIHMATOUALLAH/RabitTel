package com.rabittel.notificationservice.enums;

public enum NotificationType {

    // ===== USERS =====
    CREATE_USER,
    RESET_PASSWORD,
    CHANGE_PASSWORD,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,
    ACCOUNT_ACTIVATED,
    ACCOUNT_DEACTIVATED,

    // ===== LINES =====
    LINE_CREATED,
    LINE_UPDATED,
    LINE_DELETED,
    LINE_ASSIGNED,
    LINE_UNASSIGNED,

    // ===== CONTRACTS =====
    CONTRACT_CREATED,
    CONTRACT_UPDATED,
    CONTRACT_EXPIRED,
    CONTRACT_EXPIRING,

    // ===== IMPORT =====
    IMPORT_STARTED,
    IMPORT_COMPLETED,
    IMPORT_FAILED,

    // ===== SYSTEM =====
    SYSTEM_NOTIFICATION

}