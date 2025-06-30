package com.projetopoo.jam.dto.notification;

/**
 * Classe para retornar informações sobre o total de notificações para o frontend
 */
public class NotificationTotalResponseDTO {
    private Long notificationTotal;

    public Long getNotificationTotal() {
        return notificationTotal;
    }

    public void setNotificationTotal(Long notificationTotal) {
        this.notificationTotal = notificationTotal;
    }
}
