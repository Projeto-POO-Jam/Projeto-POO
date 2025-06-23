package com.projetopoo.jam.controller;

import com.projetopoo.jam.service.SseNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
