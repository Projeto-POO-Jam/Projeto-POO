package com.projetopoo.jam.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseNotificationService {
    private final Map<String, List<SseEmitter>> topicToEmitters = new ConcurrentHashMap<>();

    public void addEmitter(String topic, SseEmitter emitter) {
        List<SseEmitter> emitters = this.topicToEmitters.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);

        emitter.onCompletion(() -> removeEmitter(topic, emitter));
        emitter.onTimeout(() -> removeEmitter(topic, emitter));
        emitter.onError(e -> removeEmitter(topic, emitter));
    }

    private void removeEmitter(String topic, SseEmitter emitter) {
        List<SseEmitter> emitters = this.topicToEmitters.get(topic);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                this.topicToEmitters.remove(topic);
            }
        }
    }

    public void sendEventToTopic(String topic, String eventName, Object data) {
        List<SseEmitter> emitters = topicToEmitters.get(topic);
        if (emitters == null) return;

        List<SseEmitter> deadEmitters = new java.util.ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        if (!deadEmitters.isEmpty()) {
            deadEmitters.forEach(emitter -> removeEmitter(topic, emitter));
        }
    }
}