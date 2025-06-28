package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.subscribe.SubscribeResponseDTO;
import com.projetopoo.jam.dto.subscribe.SubscribeTotalResponseDTO;
import com.projetopoo.jam.dto.vote.VoteRequestDTO;
import com.projetopoo.jam.dto.vote.VoteResponseDTO;
import com.projetopoo.jam.dto.vote.VoteTotalResponseDTO;
import com.projetopoo.jam.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Classe para controlar os endpoints relacionados com votos
 */
@RestController
@RequestMapping("/api/votes")
@Tag(
        name = "Votes",
        description = "Endpoints relacionados aos votos em Jogos")
@Validated
public class VoteController {
    private final VoteService voteService;

    /**
     * Constrói uma nova instância de VoteController com suas dependências
     * @param voteService Classe service com a lógica do Vote
     */
    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/{gameId}")
    @Operation(
            summary = "Busca voto do usuário no Jogo",
            description = "Busca se o usuário já votou no Jogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voto do usuário retornado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubscribeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrada", content = @Content)
    })
    public ResponseEntity<?> findVote(@NotNull() @PathVariable Long gameId, Principal principal) {
        VoteResponseDTO voteResponseDTO = voteService.findVote(gameId, principal.getName());
        return ResponseEntity.ok(voteResponseDTO);
    }

    @PostMapping
    @Operation(
            summary = "Alterna o voto um usuário em um Jogo",
            description = "Alterna o voto de um usuário em um Jogo especificado.<br> " +
                    "Se o usuário não tiver votado no Jogo especificado, cira um novo voto para o usuário nesse Jogo.<br> " +
                    "Se o usuário já tiver votado no Jogo especificado, exclui o voto atual desse Jogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voto alternado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubscribeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrada", content = @Content)
    })
    public ResponseEntity<?> toggleVote(@Valid @RequestBody VoteRequestDTO voteRequest, Principal principal) {
        VoteResponseDTO voteResponseDTO = voteService.toggleVote(voteRequest, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(voteResponseDTO);
    }

    @GetMapping("/total/{gameId}")
    @Operation(
            summary = "Total de votos em um Jogo",
            description = "Retorna o número total de votos em um Jogo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total de votos retornado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubscribeTotalResponseDTO.class)))
    })
    public ResponseEntity<VoteTotalResponseDTO> totalVotes(@NotNull() @PathVariable Long gameId) {
        VoteTotalResponseDTO voteTotalResponseDTO = voteService.totalVotes(gameId);
        return ResponseEntity.ok(voteTotalResponseDTO);
    }

}
