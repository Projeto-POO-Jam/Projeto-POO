package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.user.UserResponseDTO;
import com.projetopoo.jam.dto.user.UserResquestDTO;
import com.projetopoo.jam.service.UserService;
import com.projetopoo.jam.exception.UserValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> findUser(@PathVariable Long userId) {
        UserResponseDTO user = userService.findUser(userId);
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
