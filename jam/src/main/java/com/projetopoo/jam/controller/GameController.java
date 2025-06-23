package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.game.GamePaginatedResponseDTO;
import com.projetopoo.jam.dto.game.GameResponseDTO;
import com.projetopoo.jam.dto.game.GameResquestDTO;
import com.projetopoo.jam.service.GameService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/games")
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> createGame(GameResquestDTO gameRequestDTO, Principal principal) {
        try {
            gameService.createGame(gameRequestDTO, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | EntityNotFoundException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<?> findGame(@PathVariable Long gameId) {
        try {
            GameResponseDTO gameResponse = gameService.findGame(gameId);
            return ResponseEntity.ok(gameResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
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
}