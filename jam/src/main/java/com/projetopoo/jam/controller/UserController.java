package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.user.UserPasswordRequestDTO;
import com.projetopoo.jam.dto.user.UserResponseDTO;
import com.projetopoo.jam.dto.user.UserRequestDTO;
import com.projetopoo.jam.dto.user.UserWithCurrentResponseDTO;
import com.projetopoo.jam.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

/**
 * Classe para controlar os endpoints relacionados com usuários
 */
@RestController
@RequestMapping("/api/users")
@Tag(
        name = "User",
        description = "Endpoints relacionados aos usuários.")
@Validated
public class UserController {
    private final UserService userService;

    /**
     * Constrói uma nova instância de UserController com suas dependências
     * @param userService Classe service com a lógica do User
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(
            summary = "Busca os dados do usuário logado",
            description = "Retorna as informações do usuário que está logado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados do usuário retornados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)))
    })
    public ResponseEntity<UserResponseDTO> findUser(Principal principal) {
        UserResponseDTO user = userService.findUser(principal.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/id/{userId}")
    @Operation(summary = "Busca um usuário por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<?> findUserId(@NotNull() @PathVariable Long userId, Principal principal) {
        UserWithCurrentResponseDTO user = userService.findUserId(userId, principal.getName());
        return ResponseEntity.ok(user);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    @Operation(
            summary = "Cria um novo usuário",
            description = "Cria um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content),
            @ApiResponse(responseCode = "409", description = "Falha na validação (username ou email já existem)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Validation failed\",\"errors\":[\"USERNAME_EXISTS\", \"EMAIL_EXISTS\"]}"))),
            @ApiResponse(responseCode = "400", description = "Erro ao processar a imagem", content = @Content)
    })
    public ResponseEntity<?> createUser(UserRequestDTO userRequestDTO) throws IOException {
        userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(consumes = { "multipart/form-data" })
    @Operation(
            summary = "Atualiza um usuário já existente",
            description = "Atualiza os detalhes de uma Jam. Apenas o proprio usuário pode editá-lo. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário alterado com sucesso", content = @Content),
            @ApiResponse(responseCode = "409", description = "Falha na validação (username ou email já existem)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Validation failed\",\"errors\":[\"USERNAME_EXISTS\", \"EMAIL_EXISTS\"]}"))),
            @ApiResponse(responseCode = "400", description = "Erro ao processar a imagem", content = @Content)
    })
    public ResponseEntity<?> updateUser(UserRequestDTO userRequestDTO, Principal principal) throws IOException {
        userService.updateUser(userRequestDTO, principal.getName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/changePassword")
    @Operation(
            summary = "Atualiza a senha do usuario",
            description = "Ele poderá alterar a senha atual para uma nova")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Senha invalida", content = @Content)
    })
    public ResponseEntity<?> updatePassword(UserPasswordRequestDTO userPasswordRequestDTO, Principal principal) {
        try {
            userService.updatePassword(userPasswordRequestDTO, principal.getName());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
