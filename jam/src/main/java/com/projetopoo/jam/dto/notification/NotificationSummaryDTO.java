package com.projetopoo.jam.dto.notification;

import java.time.LocalDateTime;

public class NotificationSummaryDTO {
    private Long notificationId;
    private String notificationMessage;
    private String notificationLink;
    private boolean notificationRead;
    private LocalDateTime notificationCreatedAt;

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationLink() {
        return notificationLink;
    }

    public void setNotificationLink(String notificationLink) {
        this.notificationLink = notificationLink;
    }

    public boolean isNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(boolean notificationRead) {
        this.notificationRead = notificationRead;
    }

    public LocalDateTime getNotificationCreatedAt() {
        return notificationCreatedAt;
    }

    public void setNotificationCreatedAt(LocalDateTime notificationCreatedAt) {
        this.notificationCreatedAt = notificationCreatedAt;
    }
}