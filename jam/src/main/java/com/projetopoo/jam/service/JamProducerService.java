package com.projetopoo.jam.service;

import com.projetopoo.jam.config.rabbitmq.JamStatusRabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JamProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void scheduleJamStatusUpdate(Long jamId, long delayInMilliseconds, String newJamStatus) {
        Map<String, Object> messageBody = Map.of(
                "jamId", jamId,
                "newJamStatus", newJamStatus
        );

        rabbitTemplate.convertAndSend(
                JamStatusRabbitMQConfig.DELAYED_EXCHANGE_NAME,
                JamStatusRabbitMQConfig.ROUTING_KEY,
                messageBody,
                message -> {
                    message.getMessageProperties().setHeader("x-delay", delayInMilliseconds);
                    return message;
                }
        );
    }

}
