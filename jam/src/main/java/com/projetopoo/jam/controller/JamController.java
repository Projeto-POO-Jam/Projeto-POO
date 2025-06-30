package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.jam.JamPaginatedResponseDTO;
import com.projetopoo.jam.dto.jam.JamRequestDTO;
import com.projetopoo.jam.dto.jam.JamResponse;
import com.projetopoo.jam.dto.jam.JamUpdateRequestDTO;
import com.projetopoo.jam.service.JamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * Classe para controlar os endpoints relacionados com jams
 */
@RestController
@RequestMapping("/api/jams")
@Tag(
        name = "Jams",
        description = "Endpoints relacionados as Jams")
@Validated
public class JamController {
    private final JamService jamService;

    /**
     * Constrói uma nova instância de JamController com suas dependências
     * @param jamService Classe service com a lógica da Jam
     */
    @Autowired
    public JamController(JamService jamService) {
        this.jamService = jamService;
    }

    @PostMapping(consumes = { "multipart/form-data" })
    @Operation(
            summary = "Cria uma nova Jam",
            description = "Registra uma nova Jam na plataforma. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Jam criada com sucesso", content = @Content),
            @ApiResponse(responseCode = "422", description = "Campos da requisição incorretos", content = @Content)
    })
    public ResponseEntity<?> createJam(JamRequestDTO jamRequestDTO, Principal principal) throws IOException {
        jamService.createJam(jamRequestDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
            @ApiResponse(responseCode = "404", description = "Jam não encontrada", content = @Content)
    })
    public ResponseEntity<?> findJam(@NotNull() @PathVariable Long jamId, Principal principal) {
        JamResponse jamResponse = jamService.findJam(jamId, principal.getName());
        return ResponseEntity.ok(jamResponse);
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
                            schema = @Schema(implementation = JamPaginatedResponseDTO.class)))
    })
    public ResponseEntity<?> listJams(
            @NotNull() @Parameter(example = "03-2025") @RequestParam String month,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        JamPaginatedResponseDTO response = jamService.findJamsList(month, offset, limit);
        return ResponseEntity.ok(response);
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
                            schema = @Schema(implementation = JamPaginatedResponseDTO.class)))
    })
    public ResponseEntity<?> bannerJams(@RequestParam(defaultValue = "6") int limit) {
        JamPaginatedResponseDTO response = jamService.findJamsBanner(limit);
        return ResponseEntity.ok(response);
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
                            schema = @Schema(implementation = JamUpdateRequestDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário não é o autor da Jam.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Jam não encontrada", content = @Content),
            @ApiResponse(responseCode = "422", description = "Campos da requisição incorretos", content = @Content)
    })
    public ResponseEntity<?> updateJam(@Valid JamUpdateRequestDTO jamUpdateRequestDTO, Principal principal) throws IOException {
        jamService.updateJam(jamUpdateRequestDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
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
            @NotNull() @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        JamPaginatedResponseDTO response = jamService.findJamListByUserId(userId, offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/createUser")
    @Operation(
            summary = "Lista todos os jam criados pelo usuário",
            description = "Retorna uma lista paginada de todos os jam criadas pelo usuário em ordem de quantidade de inscritos.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jam listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JamPaginatedResponseDTO.class)))
    })
    public ResponseEntity<?> findJamListByUserId(
            @NotNull() @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        JamPaginatedResponseDTO response = jamService.findMyJamListByUserId(userId, offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Busca Jams por título",
            description = "Retorna uma lista paginada de Jams cujo título contém o texto de busca.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jams encontradas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JamPaginatedResponseDTO.class)))
    })
    public ResponseEntity<?> searchJams(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        JamPaginatedResponseDTO response = jamService.searchJamsByTitle(query, offset, limit);
        return ResponseEntity.ok(response);
    }
}
