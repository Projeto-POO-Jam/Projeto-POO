package com.projetopoo.jam.dto.notification;

import java.util.List;

public class NotificationPaginatedResponseDTO {
    private List<NotificationSummaryDTO> notifications;
    private long totalUnreadCount;

    public NotificationPaginatedResponseDTO(List<NotificationSummaryDTO> notifications, long totalUnreadCount) {
        this.notifications = notifications;
        this.totalUnreadCount = totalUnreadCount;
    }

    public List<NotificationSummaryDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationSummaryDTO> notifications) {
        this.notifications = notifications;
    }

    public long getTotalUnreadCount() {
        return totalUnreadCount;
    }

    public void setTotalUnreadCount(long totalUnreadCount) {
        this.totalUnreadCount = totalUnreadCount;
    }
}