package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.game.GamePaginatedResponseDTO;
import com.projetopoo.jam.dto.game.GameResponseDTO;
import com.projetopoo.jam.dto.game.GameRequestDTO;
import com.projetopoo.jam.dto.game.GameUpdateRequestDTO;
import com.projetopoo.jam.dto.jam.JamUpdateRequestDTO;
import com.projetopoo.jam.service.GameService;
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

import java.io.IOException;
import java.security.Principal;

/**
 * Classe para controlar os endpoints relacionados com jogos
 */
@RestController
@RequestMapping("/api/games")
@Tag(
        name = "Games",
        description = "Endpoints relacionados aos jogos")
@Validated
public class GameController {
    private final GameService gameService;

    /**
     * Constrói uma nova instância de GameController com suas dependências
     * @param gameService Classe service com a lógica do Game
     */
    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping(consumes = { "multipart/form-data" })
    @Operation(
            summary = "Cria um novo jogo a uma Jam",
            description = "Cria um novo jogo vinculada a uma Jam. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Jogo criado com sucesso", content = @Content),
            @ApiResponse(responseCode = "422", description = "Campos da requisição incorretos", content = @Content)
    })
    public ResponseEntity<?> createGame(@Valid GameRequestDTO gameRequestDTO, Principal principal) throws IOException {
        gameService.createGame(gameRequestDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{gameId}")
    @Operation(
            summary = "Busca um jogo pelo ID",
            description = "Retorna os detalhes de um jogo específico.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jogo encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Jogo não encontrado", content = @Content)
    })
    public ResponseEntity<?> findGame(@NotNull() @PathVariable Long gameId, Principal principal) {
        GameResponseDTO gameResponse = gameService.findGame(gameId, principal.getName());
        return ResponseEntity.ok(gameResponse);
    }

    @GetMapping("/list")
    @Operation(
            summary = "Lista todos os jogos de uma Jam",
            description = "Retorna uma lista paginada de todos os jogos submetidos a uma Jam específica.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jogos listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GamePaginatedResponseDTO.class)))
    })
    public ResponseEntity<?> listGames(
            @NotNull() @RequestParam Long jamId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        GamePaginatedResponseDTO response = gameService.findGameList(jamId, offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @Operation(
            summary = "Lista todos os jogos de um usuário",
            description = "Retorna uma lista paginada de todos os jogos de um usuário em ordem do total de votos.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jogos listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GamePaginatedResponseDTO.class)))
    })
    public ResponseEntity<?> findGameListByUserId(
            @NotNull()  @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        GamePaginatedResponseDTO response = gameService.findGameListByUserId(userId, offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/complete")
    @Operation(
            summary = "Lista todos os jogos que existem",
            description = "Retorna uma lista paginada de todos os jogos que existem em ordem do total de votos.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jogos listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GamePaginatedResponseDTO.class)))
    })
    public ResponseEntity<?> findGameCompleteList(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        GamePaginatedResponseDTO response = gameService.findGameCompleteList(offset, limit);
        return ResponseEntity.ok(response);
    }

    @PutMapping(consumes = { "multipart/form-data" })
    @Operation(
            summary = "Atualiza um jogo existente",
            description = "Atualiza os detalhes de um jogo. Apenas o criador do jogo pode editá-lo. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jam atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JamUpdateRequestDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário não é o autor do jogo.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrada", content = @Content),
            @ApiResponse(responseCode = "422", description = "Campos da requisição incorretos", content = @Content)
    })
    public ResponseEntity<?> updateGame(@Valid GameUpdateRequestDTO gameUpdateRequestDTO, Principal principal) throws IOException {
        gameService.updateGame(gameUpdateRequestDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user/vote")
    @Operation(
            summary = "Lista todos os jogos de um usuário votou",
            description = "Retorna uma lista paginada de todos os jogos que ele votou de um usuário em ordem do banco de dados.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jogos listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GamePaginatedResponseDTO.class)))
    })
    public ResponseEntity<?> findGameListByUserIdVote(
            @NotNull()  @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        GamePaginatedResponseDTO response = gameService.findGameListByUserIdVote(userId, offset, limit);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{gameId}")
    @Operation(
            summary = "Exclui um game",
            description = "Exclui um game pelo seu ID. Apenas o autor do game pode realizar esta ação. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "game excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário não é o autor do Game.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Game não encontrado", content = @Content)
    })
    public ResponseEntity<?> deleteGame(@PathVariable Long gameId, Principal principal) throws IOException {
        gameService.deleteGame(gameId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}