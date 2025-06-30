package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.notification.NotificationSummaryDTO;
import com.projetopoo.jam.dto.notification.NotificationPaginatedResponseDTO;
import com.projetopoo.jam.model.Jam;
import com.projetopoo.jam.model.JamStatus;
import com.projetopoo.jam.model.Notification;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.NotificationRepository;
import com.projetopoo.jam.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SseNotificationService sseNotificationService;

    /**
     * Busca um lote de notificações não lidas e a contagem total.
     * @param identifier O email ou username do usuário.
     * @param offset O número de itens a pular.
     * @param limit O número máximo de itens a retornar.
     * @return um DTO contendo a lista de notificações e o total não lido.
     */
    @Transactional(readOnly = true)
    public NotificationPaginatedResponseDTO getUnreadNotificationsWithCount(String identifier, int offset, int limit) {
        User user = userRepository.findByIdentifier(identifier);

        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by("notificationCreatedAt").descending());

        Page<Notification> notificationPage = notificationRepository.findByNotificationUserAndNotificationReadFalse(user, pageable);

        List<NotificationSummaryDTO> dtos = notificationPage.getContent().stream()
                .map(notification -> modelMapper.map(notification, NotificationSummaryDTO.class))
                .collect(Collectors.toList());

        long totalCount = notificationPage.getTotalElements();

        return new NotificationPaginatedResponseDTO(dtos, totalCount);
    }

    @Transactional
    public void markAsRead(Long notificationId, String identifier) {
        User user = userRepository.findByIdentifier(identifier);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada"));

        if (!notification.getNotificationUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Você não tem permissão para alterar esta notificação.");
        }

        notification.setNotificationRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void createAndSendJamStatusNotification(Jam jam, JamStatus newStatus) {
        String message;
        String jamTitle = jam.getJamTitle();

        switch (newStatus) {
            case ACTIVE:
                message = "A jam '" + jamTitle + "' começou!";
                break;
            case FINISHED:
                message = "A jam '" + jamTitle + "' terminou.";
                break;
            default:
                message = "O status da jam '" + jamTitle + "' foi atualizado para: " + newStatus;
                break;
        }

        String link = "/jams/" + jam.getJamId();

        jam.getJamSubscribes().forEach(subscribe -> {
            User userToNotify = subscribe.getSubscribeUser();

            Notification notification = new Notification();
            notification.setNotificationUser(userToNotify);
            notification.setNotificationMessage(message);
            notification.setNotificationLink(link);
            notification.setNotificationCreatedAt(LocalDateTime.now());
            Notification savedNotification = notificationRepository.save(notification);

            NotificationSummaryDTO dto = modelMapper.map(savedNotification, NotificationSummaryDTO.class);
            sseNotificationService.sendEventToTopic("new-notification","user-notifications-" + userToNotify.getUserId(), dto);
        });
    }
}