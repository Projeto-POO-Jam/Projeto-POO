package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.notification.NotificationPaginatedResponseDTO;
import com.projetopoo.jam.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Endpoints para gerenciar notificações")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationPaginatedResponseDTO> getUnreadNotifications(
            Principal principal,
            //Parâmetros alterados para limit e offset
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        NotificationPaginatedResponseDTO response = notificationService.getUnreadNotificationsWithCount(principal.getName(), offset, limit);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        notificationService.markAsRead(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}