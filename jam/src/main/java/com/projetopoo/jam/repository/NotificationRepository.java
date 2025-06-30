package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Notification;
import com.projetopoo.jam.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByNotificationUserAndNotificationReadFalse(User notificationUser, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET " +
                "n.notificationRead = true " +
            "WHERE " +
                "n.notificationUser = :user " +
                "AND n.notificationRead = false")
    void markAllAsReadForUser(@Param("user") User user);
}