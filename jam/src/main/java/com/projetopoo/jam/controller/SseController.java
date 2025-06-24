package com.projetopoo.jam.controller;

import com.projetopoo.jam.service.SseNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/events")
@Tag(
        name = "SSE Events",
        description = "Endpoints para notificações em tempo real usando Server-Sent Events (SSE).")
public class SseController {
    @Autowired
    private SseNotificationService sseNotificationService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "Inscreve-se em um tópico de eventos",
            description = "Estabelece uma conexão SSE para receber atualizações em tempo real sobre um tópico específico. O cliente deve especificar o tópico ao qual deseja se conectar.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conexão estabelecida com sucesso. O stream de eventos será enviado.",
                    content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE))
    })
    public SseEmitter handleSseEvents(@RequestParam String topic) {
        SseEmitter emitter = new SseEmitter(3600000L);
        sseNotificationService.addEmitter(topic, emitter);
        return emitter;
    }

}
