package com.projetopoo.jam.dto.notification;

import java.time.LocalDateTime;

public class NotificationSummaryDTO {
    private Long notificationId;
    private String notificationMessage;
    private String notificationLink;
    private Boolean notificationRead;
    private LocalDateTime notificationDate;

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

    public Boolean getNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(Boolean notificationRead) {
        this.notificationRead = notificationRead;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }
}