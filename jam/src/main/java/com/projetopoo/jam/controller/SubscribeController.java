package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.subscribe.SubscribeRequestDTO;
import com.projetopoo.jam.dto.subscribe.SubscribeResponseDTO;
import com.projetopoo.jam.dto.subscribe.SubscribeTotalResponseDTO;
import com.projetopoo.jam.service.SubscribeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Classe para controlar os endpoints relacionados com inscrições
 */
@RestController
@RequestMapping("/api/subscribes")
@Tag(
        name = "Subscriptions",
        description = "Endpoints relacionados as inscrições em Jams")
@Validated
public class SubscribeController {
    private final SubscribeService subscribeService;

    /**
     * Constrói uma nova instância de SubscribeController com suas dependências
     * @param subscribeService Classe service com a lógica do Subscribe
     */
    @Autowired
    public SubscribeController(SubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    @PostMapping
    @Operation(
            summary = "Alterna a inscrição um usuário em uma Jam",
            description = "Alterna a inscrição de um usuário em uma Jam especificada.<br> " +
                    "Se o usuário não estiver inscrito na Jam especificada, cira uma nova inscrição para o usuário nessa Jam.<br> " +
                    "Se o usuário já estiver inscrito na Jam especificada, exclui a inscrição atual dessa Jam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscrição alternada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubscribeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Jam não encontrada", content = @Content)
    })
    public ResponseEntity<?> toggleSubscribe(@Valid @RequestBody SubscribeRequestDTO subscribeRequestDTO, Principal principal) {
        SubscribeResponseDTO subscribeResponseDTO = subscribeService.toggleSubscribe(subscribeRequestDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(subscribeResponseDTO);
    }

    @GetMapping("/total/{jamId}")
    @Operation(
            summary = "Total de inscrições em uma Jam",
            description = "Retorna o número total de inscrições em uma Jam específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total de inscrições retornado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubscribeTotalResponseDTO.class)))
    })
    public ResponseEntity<SubscribeTotalResponseDTO> totalSubscribe(@NotNull() @PathVariable Long jamId) {
        SubscribeTotalResponseDTO subscribeTotalResponseDTO = subscribeService.totalSubscribes(jamId);
        return ResponseEntity.ok(subscribeTotalResponseDTO);
    }

    @GetMapping("/{jamId}")
    @Operation(
            summary = "Busca inscrição do usuário na Jam",
            description = "Busca se o usuário já está inscrito na Jam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscrição do usuário retornado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubscribeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Jam não encontrada", content = @Content)
    })
    public ResponseEntity<?> findSubscribe(@NotNull() @PathVariable Long jamId, Principal principal) {
        SubscribeResponseDTO subscribeResponseDTO = subscribeService.findSubscribe(jamId, principal.getName());
        return ResponseEntity.ok(subscribeResponseDTO);
    }
}
