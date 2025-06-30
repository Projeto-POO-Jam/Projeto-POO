package com.projetopoo.jam.dto.notification;

import java.util.List;

public class NotificationPaginatedResponseDTO {
    private List<NotificationSummaryDTO> notifications;
    private Long total;

    public NotificationPaginatedResponseDTO(List<NotificationSummaryDTO> notifications, Long total) {
        this.notifications = notifications;
        this.total = total;
    }

    public List<NotificationSummaryDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationSummaryDTO> notifications) {
        this.notifications = notifications;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}