package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.jam.JamPaginatedResponseDTO;
import com.projetopoo.jam.dto.jam.JamRequestDTO;
import com.projetopoo.jam.dto.jam.JamResponse;
import com.projetopoo.jam.service.JamService;
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
public class JamController {
    @Autowired
    private JamService jamService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> createJam(JamRequestDTO jamRequestDTO, Principal principal) {
        try {
            jamService.createJam(jamRequestDTO, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{jamId}")
    public ResponseEntity<?> findJam(@PathVariable Long jamId) {
        try {
            JamResponse jamResponse = jamService.findJam(jamId);
            return ResponseEntity.ok(jamResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(consumes = { "multipart/form-data" })
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



    @GetMapping("/list")
    public ResponseEntity<?> listJams(
            @RequestParam String month,
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
    public ResponseEntity<?> bannerJams(@RequestParam(defaultValue = "6") int limit) {
        try {
            JamPaginatedResponseDTO response = jamService.findJamsBanner(limit);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
