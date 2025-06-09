package com.projetopoo.jam.controller;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestPart("user") User user,
            @RequestPart(value = "userPhoto", required = false) MultipartFile photo) {
        try {
            userService.createUser(user, photo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @RequestPart("user") User user,
            @RequestPart(value = "userPhoto", required = false) MultipartFile photo) {
        try{
            userService.updateUser(user, photo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
