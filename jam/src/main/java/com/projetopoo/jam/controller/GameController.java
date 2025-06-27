package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.game.GamePaginatedResponseDTO;
import com.projetopoo.jam.dto.game.GameResponseDTO;
import com.projetopoo.jam.dto.game.GameResquestDTO;
import com.projetopoo.jam.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/games")
@Tag(
        name = "Games",
        description = "Endpoints relacionados aos jogos")
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping(consumes = { "multipart/form-data" })
    @Operation(
            summary = "Cria um novo jogo a uma Jam",
            description = "Cria um novo jogo vinculada a uma Jam. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Jogo criado com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou erro no upload de arquivos", content = @Content)
    })
    public ResponseEntity<?> createGame(GameResquestDTO gameRequestDTO, Principal principal) {
        try {
            gameService.createGame(gameRequestDTO, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | EntityNotFoundException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    public ResponseEntity<?> findGame(@PathVariable Long gameId, Principal principal) {
        try {
            GameResponseDTO gameResponse = gameService.findGame(gameId, principal.getName());
            return ResponseEntity.ok(gameResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
            @RequestParam Long jamId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            GamePaginatedResponseDTO response = gameService.findGameList(jamId, offset, limit);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            GamePaginatedResponseDTO response = gameService.findGameListByUserId(userId, offset, limit);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
  
      @PutMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateGame(GameResquestDTO gameRequestDTO, Principal principal) {
        try {
            gameService.updateGame(gameRequestDTO, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}