package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.*;
import com.projetopoo.jam.service.SubscribeService;
import com.projetopoo.jam.service.VoteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/subscribes")
public class SubscribeController {
    @Autowired
    private SubscribeService subscribeService;

    @PostMapping
    public ResponseEntity<?> toggleSubscribe(@RequestBody SubscribeRequestDTO subscribeRequestDTO, Principal principal) {
        try {
            SubscribeResponseDTO subscribeResponseDTO = subscribeService.toggleSubscribe(subscribeRequestDTO, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(subscribeResponseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/total/{jamId}")
    public ResponseEntity<SubscribeTotalResponseDTO> totalSubscribe(@PathVariable Long jamId) {
        SubscribeTotalResponseDTO subscribeTotalResponseDTO = subscribeService.totalSubscribes(jamId);
        return ResponseEntity.ok(subscribeTotalResponseDTO);
    }

}
