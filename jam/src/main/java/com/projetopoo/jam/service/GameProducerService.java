package com.projetopoo.jam.service;

import com.projetopoo.jam.config.rabbitmq.GameExtractRabbitMQConfig;
import com.projetopoo.jam.config.rabbitmq.JamStatusRabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GameProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
