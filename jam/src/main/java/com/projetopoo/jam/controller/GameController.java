package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.*;
import com.projetopoo.jam.service.CommentService;
import com.projetopoo.jam.service.GameService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /*
    @GetMapping("/list")
    public ResponseEntity<?> listJams(
            @RequestParam int jamId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<GameResponseDTO> comments = gameService.findGameList(jamId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    */

}