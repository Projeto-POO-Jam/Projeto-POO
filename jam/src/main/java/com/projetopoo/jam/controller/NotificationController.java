package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.jam.JamPaginatedResponseDTO;
import com.projetopoo.jam.dto.notification.NotificationPaginatedResponseDTO;
import com.projetopoo.jam.dto.notification.NotificationTotalResponseDTO;
import com.projetopoo.jam.dto.subscribe.SubscribeTotalResponseDTO;
import com.projetopoo.jam.dto.vote.VoteTotalResponseDTO;
import com.projetopoo.jam.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Classe para controlar os endpoints relacionados com notificações
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(
        name = "Notifications",
        description = "Endpoints para gerenciar notificações"
)
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(
            summary = "Busca notificações do usuário logado",
            description = "Retorna uma lista paginada de todas as notificações do usuário logado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificações listadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JamPaginatedResponseDTO.class)))
    })
    public ResponseEntity<NotificationPaginatedResponseDTO> listNotifications(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            Principal principal) {

        NotificationPaginatedResponseDTO response = notificationService.listNotifications(offset, limit, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/read")
    @Operation(
            summary = "Marca notificações do usuário logado como lidas",
            description = "Atualiza o status das notificações como lidas.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificações atualizadas com sucesso",
                    content = @Content)
    })
    public ResponseEntity<Void> markAsRead(Principal principal) {
        notificationService.markAsRead(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    @Operation(
            summary = "Total de notificações não lidas do usuário",
            description = "Retorna o número total de notificações não lidas do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total de notificações retornado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubscribeTotalResponseDTO.class)))
    })
    public ResponseEntity<NotificationTotalResponseDTO> totalNotification(Principal principal) {
        NotificationTotalResponseDTO notificationTotalResponseDTO = notificationService.totalNotifications(principal.getName());
        return ResponseEntity.ok(notificationTotalResponseDTO);
    }
}