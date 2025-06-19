package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.VoteRequestDTO;
import com.projetopoo.jam.dto.VoteResponseDTO;
import com.projetopoo.jam.dto.VoteTotalResponseDTO;
import com.projetopoo.jam.exception.UserValidationException;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.service.UserService;
import com.projetopoo.jam.service.VoteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    @Autowired
    private VoteService voteService;

    @GetMapping("/{gameId}")
    public ResponseEntity<?> findVote(@PathVariable Long gameId, Principal principal) {
        try {
            VoteResponseDTO voteResponseDTO = voteService.findVote(gameId, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(voteResponseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> toggleVote(@RequestBody VoteRequestDTO voteRequest, Principal principal) {
        try {
            VoteResponseDTO voteResponseDTO = voteService.toggleVote(voteRequest, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(voteResponseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/total/{gameId}")
    public ResponseEntity<VoteTotalResponseDTO> totalVotes(@PathVariable Long gameId) {
        VoteTotalResponseDTO voteTotalResponseDTO = voteService.totalVotes(gameId);
        return ResponseEntity.ok(voteTotalResponseDTO);
    }

}
