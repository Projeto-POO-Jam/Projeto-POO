package com.projetopoo.jam.controller;

import com.projetopoo.jam.dto.JamRequestDTO;
import com.projetopoo.jam.service.JamService;
import com.projetopoo.jam.service.SseNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/events")
public class SseController {
    @Autowired
    private SseNotificationService sseNotificationService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleSseEvents(@RequestParam String topic) {
        SseEmitter emitter = new SseEmitter(3600000L);
        sseNotificationService.addEmitter(topic, emitter);
        return emitter;
    }

}
