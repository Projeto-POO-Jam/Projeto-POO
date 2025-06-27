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
                    description = """
                            Conexão SSE estabelecida com sucesso. O servidor começará a enviar eventos para o cliente.
                            Cada evento segue o formato `event: <nome-do-evento>` seguido por `data: <payload-json>`.
                            
                            ---
                            
                            ### Tópico: `jams-list-update`
                            Eventos relacionados a atualizações na lista geral de Jams.
                            
                            * **Evento:** `jam-insert`
                                * **Descrição:** Notifica quando uma nova Jam é criada.
                                * **Payload (`data`):** Um objeto `JamSseDTO`.
                            * **Evento:** `jam-subscribes-update`
                                * **Descrição:** Notifica quando o número de inscritos em uma Jam é alterado.
                                * **Payload (`data`):** Um objeto `SubscribeSseDTO`.
                            * **Evento:** `jam-status-update`
                                * **Descrição:** Notifica quando o status de uma Jam é alterado.
                                * **Payload (`data`):** Um objeto `JamSseDTO`.

                            ---
                            
                            ### Tópico: `jams-update`
                            Eventos relacionados a atualizações de uma Jam.
                            
                            * **Evento:** `jam-subscribes-update-{jamId}`
                                * **Descrição:** Notifica quando o total de inscritos em uma Jam é alterado.
                                * **Payload (`data`):** Um objeto `SubscribeTotalResponseDTO`.
                            * **Evento:** `jam-status-update-{jamId}`
                                * **Descrição:** Notifica quando o status da Jam é alterado.
                                * **Payload (`data`):** Um objeto `JamSseDTO`.
                            
                            ---
                            
                            ### Tópico: `games-update`
                            Eventos relacionados a atualizações em jogos.
                            
                            * **Evento:** `votes-update-{gameId}`
                                * **Descrição:** Notifica quando o total de votos em um jogo é alterado.
                                * **Payload (`data`):** Um objeto `VoteTotalResponseDTO`.
                            * **Evento:** `comments-update-{gameId}`
                                * **Descrição:** Notifica quando um novo comentario é feito no jogo.
                                * **Payload (`data`):** Um objeto `CommentResponseDTO`.
                            * **Evento:** `comments-delete-{gameId}`
                                * **Descrição:** Notifica quando um comentario é excluido no jogo.
                                * **Payload (`data`):** Um objeto `CommentResponseDTO`.
                            """,
                    content = @Content

            )
    })
    public SseEmitter handleSseEvents(@RequestParam String topic) {
        SseEmitter emitter = new SseEmitter(3600000L);
        sseNotificationService.addEmitter(topic, emitter);
        return emitter;
    }

}
