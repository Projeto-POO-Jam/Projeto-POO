package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Notification;
import com.projetopoo.jam.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByNotificationUserAndNotificationReadFalse(User notificationUser, Pageable pageable);
}