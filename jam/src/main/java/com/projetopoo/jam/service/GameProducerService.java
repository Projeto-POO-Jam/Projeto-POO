package com.projetopoo.jam.service;

import com.projetopoo.jam.config.rabbitmq.GameExtractRabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Classe que faz responsável por adicionar jogos a fila do RabbitMQ para descompactar
 */
@Service
public class GameProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public GameProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void scheduleGameStatusUpdate(Long gameId, String gameToken) {
        Map<String, Object> messageBody = Map.of(
                "gameId", gameId,
                "gameToken", gameToken
        );

        rabbitTemplate.convertAndSend(
                GameExtractRabbitMQConfig.EXCHANGE_NAME,
                GameExtractRabbitMQConfig.ROUTING_KEY,
                messageBody
        );
    }

}
