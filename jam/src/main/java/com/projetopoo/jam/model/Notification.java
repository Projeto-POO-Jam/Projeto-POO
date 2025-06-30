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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User notificationUser;

    @Column(nullable = false)
    private String notificationMessage;

    @Column
    private String notificationLink;

    @Column(name = "notification_is_read", nullable = false)
    private boolean notificationRead = false;

    @Column(name = "notification_created_at", nullable = false)
    private LocalDateTime notificationCreatedAt;

    //Getters e Setters
    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public User getNotificationUser() {
        return notificationUser;
    }

    public void setNotificationUser(User notificationUser) {
        this.notificationUser = notificationUser;
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