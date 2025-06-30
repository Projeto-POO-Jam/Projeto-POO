package com.projetopoo.jam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Classe model de notificação, responsável pela notificação do sistema.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private String notificationMessage;

    @Column
    private String notificationLink;

    @Column(nullable = false)
    private boolean notificationRead = false;

    @Column(nullable = false)
    private LocalDateTime notificationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User notificationUser;

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

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    public User getNotificationUser() {
        return notificationUser;
    }

    public void setNotificationUser(User notificationUser) {
        this.notificationUser = notificationUser;
    }
}