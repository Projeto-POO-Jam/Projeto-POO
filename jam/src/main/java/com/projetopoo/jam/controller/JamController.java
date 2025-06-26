package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.game.GamePaginatedResponseDTO;
import com.projetopoo.jam.dto.jam.JamPaginatedResponseDTO;
import com.projetopoo.jam.dto.jam.JamRequestDTO;
import com.projetopoo.jam.dto.jam.JamResponse;
import com.projetopoo.jam.service.JamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/jams")
@Tag(
        name = "Jams",
        description = "Endpoints relacionados as Jams")
public class JamController {
    @Autowired
    private JamService jamService;

    @PostMapping(consumes = { "multipart/form-data" })
    @Operation(
            summary = "Cria uma nova Jam",
            description = "Registra uma nova Jam na plataforma. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Jam criada com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou erro no upload da imagem", content = @Content),
    })
    public ResponseEntity<?> createJam(JamRequestDTO jamRequestDTO, Principal principal) {
        try {
            jamService.createJam(jamRequestDTO, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{jamId}")
    @Operation(
            summary = "Busca uma Jam pelo ID",
            description = "Retorna os detalhes de uma Jam específica.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jam encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JamResponse.class))),
            @ApiResponse(responseCode = "400", description = "Jam não encontrada", content = @Content)
    })
    public ResponseEntity<?> findJam(@PathVariable Long jamId) {
        try {
            JamResponse jamResponse = jamService.findJam(jamId);
            return ResponseEntity.ok(jamResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(
            summary = "Busca Jams de um mês específico",
            description = "Retorna uma lista paginada de todas as Jams de um mês especifico.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jams do mês listadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JamPaginatedResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    })
    public ResponseEntity<?> listJams(
            @Parameter(example = "03-2025") @RequestParam String month,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            JamPaginatedResponseDTO response = jamService.findJamsList(month, offset, limit);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/banner")
    @Operation(
            summary = "Busca as principais Jams com maior numero de inscrições",
            description = "Retorna os detalhes das Jams ativas ou agendadas que possuem maior numero de inscritos em ordem de inscrição.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jams com maior numero de inscritos listadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JamPaginatedResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    })
    public ResponseEntity<?> bannerJams(@RequestParam(defaultValue = "6") int limit) {
        try {
            JamPaginatedResponseDTO response = jamService.findJamsBanner(limit);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(consumes = { "multipart/form-data" })
    @Operation(
            summary = "Atualiza uma Jam existente",
            description = "Atualiza os detalhes de uma Jam. Apenas o criador da Jam pode editá-la. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jam atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JamRequestDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário não é o autor da Jam.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Jam não encontrada", content = @Content)
    })
    public ResponseEntity<?> updateJam(JamRequestDTO jamRequestDTO, Principal principal) {
        try {
            jamService.updateJam(jamRequestDTO, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/user")
    @Operation(
            summary = "Lista todos os jam de um usuário",
            description = "Retorna uma lista paginada de todos os jam de um usuário em ordem de id.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jam listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JamPaginatedResponseDTO.class)))
    })
    public ResponseEntity<?> findGameListByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            JamPaginatedResponseDTO response = jamService.findJamListByUserId(userId, offset, limit);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
