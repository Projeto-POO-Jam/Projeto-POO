package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.comment.CommentRequestDTO;
import com.projetopoo.jam.dto.comment.CommentResponseDTO;
import com.projetopoo.jam.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@Tag(
        name = "Comentários",
        description = "Endpoints relacionados aos comentários de jogos")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    @Operation(
            summary = "Cria um novo comentário",
            description = "Adiciona um comentário a um jogo. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comentário criado com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    })
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO commentRequestDTO, Principal principal) {
        try {
            commentService.createComment(commentRequestDTO, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list/{gameId}")
    @Operation(
            summary = "Lista comentários de um jogo",
            description = "Retorna todos os comentários de um jogo específico.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comentários listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponseDTO.class)))
    })
    public ResponseEntity<List<CommentResponseDTO>> findCommentsList(@PathVariable Long gameId, Principal principal) {
        List<CommentResponseDTO> comments = commentService.findCommentsList(gameId, principal.getName());
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    @Operation(
            summary = "Exclui um comentário",
            description = "Exclui um comentário pelo seu ID. Apenas o autor do comentário pode realizar esta ação. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comentário excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário não é o autor do comentário.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado", content = @Content)
    })
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Principal principal) {
        try {
            commentService.deleteComment(commentId, principal.getName());
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

}
