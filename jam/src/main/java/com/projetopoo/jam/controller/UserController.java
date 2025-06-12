package com.projetopoo.jam.controller;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.projetopoo.jam.dto.UserResponseDTO;
import com.projetopoo.jam.dto.UserResquestDTO;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.service.UserService;
import com.projetopoo.jam.exception.UserValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDTO> findUser(Principal principal) {
        UserResponseDTO user = userService.findUser(principal.getName());
        return ResponseEntity.ok(user);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> createUser(UserResquestDTO userResquestDTO) {
        try {
            userService.createUser(userResquestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserValidationException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validation failed");
            errorResponse.put("errors", e.getErrors());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateUser(UserResquestDTO userResquestDTO, Principal principal) {
        try{
            userService.updateUser(userResquestDTO, principal.getName());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
