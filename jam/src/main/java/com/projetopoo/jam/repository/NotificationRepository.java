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

/**
 * Interface repository para classe Notification, responsável pelas funções relacionadas ao banco de dados
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * Função que busca uma lista paginada de notificações, com base no usuário
     * @param notificationUser Usuário que está sendo usada na consulta
     * @param pageable Informações sobre como deve ser feita a paginação
     * @return Lista paginada contendo diversas notificações que correspondem a busca
     */
    Page<Notification> findByNotificationUser(User notificationUser, Pageable pageable);

    /**
     * Função que marca todas as notificações que não foram lidas pelo usuário como lidas
     * @param notificationUser Usuário que está sendo usada na consulta
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET " +
                "n.notificationRead = true " +
            "WHERE " +
                "n.notificationUser = :notificationUser " +
                "AND n.notificationRead = false")
    void markAllAsReadForUser(@Param("notificationUser") User notificationUser);

    /**
     * Função que busca o total de notificações não lidas de um usuário
     * @param notificationUser Usuário que está sendo usada na consulta
     * @return Quantidade notificações não lidas
     */
    Long countByNotificationUserAndNotificationReadFalse(User notificationUser);
}