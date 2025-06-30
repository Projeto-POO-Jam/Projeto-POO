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
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SseNotificationService sseNotificationService;

    /**
     * Constrói uma nova instância de NotificationService com suas dependências
     * @param notificationRepository Repository para comunicação com o banco de dados da classe Notification
     * @param userRepository Repository para comunicação com o banco de dados da classe User
     * @param modelMapper Classe para mapear transformações entre models e DTOs
     * @param sseNotificationService Classe para envio de eventos via SSE
     */
    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               ModelMapper modelMapper,
                               SseNotificationService sseNotificationService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.sseNotificationService = sseNotificationService;
    }

    /**
     * Busca todas as notificações do usurário logado
     * @param offset O número de itens a pular
     * @param limit O número máximo de itens a retornar
     * @param identifier Identificador do usuário
     * @return Uma lista paginada de notificações e o total de notificações
     */
    @Transactional(readOnly = true)
    public NotificationPaginatedResponseDTO listNotifications(int offset, int limit, String identifier) {
        // Busca usuário que fez a requisição
        User user = userRepository.findByIdentifier(identifier);

        // Define qual é a pagina de interesse
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by("notificationCreatedAt").descending());

        // Busca a lista paginada de notificações
        Page<Notification> notificationPage = notificationRepository.findByNotificationUserAndNotificationReadFalse(user, pageable);

        // Passa a lista paginada para o formato da resposta
        List<NotificationSummaryDTO> listNotificationSummaryDTO = notificationPage.getContent().stream()
                .map(notification -> modelMapper.map(notification, NotificationSummaryDTO.class))
                .collect(Collectors.toList());

        return new NotificationPaginatedResponseDTO(listNotificationSummaryDTO, notificationPage.getTotalElements());
    }

    /**
     * Marca as notificações de um usuário como visualizadas
     * @param notificationId Id da notificação visualizadas
     * @param identifier Identificador do usuário
     */
    @Transactional
    public void markAsRead(Long notificationId, String identifier) {
        // Busca usuário que fez a requisição
        User user = userRepository.findByIdentifier(identifier);

        // Busca notificações
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada"));

        if (!notification.getNotificationUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Você não tem permissão para alterar esta notificação.");
        }

        // Altera o status para lido
        notification.setNotificationRead(true);

        // Salva a notificação
        notificationRepository.save(notification);
    }

    /**
     * Cria uma nova notificação de atualização de status da jam
     * @param jam Jam que teve o status modificado
     * @param newStatus Novo status da jam
     */
    @Transactional
    public void createJamStatusNotification(Jam jam, JamStatus newStatus) {
        String message;
        String jamTitle = jam.getJamTitle();

        // Cria a mensagem para a notificação com base no novo status da jam
        message = switch (newStatus) {
            case ACTIVE -> "A jam '" + jamTitle + "' começou!";
            case FINISHED -> "A jam '" + jamTitle + "' terminou.";
            default -> "O status da jam '" + jamTitle + "' foi atualizado para: " + newStatus;
        };

        // Monta o link da jam
        String link = "/jams/" + jam.getJamId();

        // Loop para enviar a notificação para todos os usuários inscritos na jam
        jam.getJamSubscribes().forEach(subscribe -> {
            User userToNotify = subscribe.getSubscribeUser();

            Notification notification = new Notification();
            notification.setNotificationUser(userToNotify);
            notification.setNotificationMessage(message);
            notification.setNotificationLink(link);
            notification.setNotificationDate(LocalDateTime.now());
            Notification savedNotification = notificationRepository.save(notification);

            NotificationSummaryDTO dto = modelMapper.map(savedNotification, NotificationSummaryDTO.class);
            sseNotificationService.sendEventToTopic("notification-update","user-notifications-" + userToNotify.getUserId(), dto);
        });
    }
}