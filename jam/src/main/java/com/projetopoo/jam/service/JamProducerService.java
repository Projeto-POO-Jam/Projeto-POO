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

    public void scheduleJamStatusUpdate(Long jamId, long delayInMilliseconds, String newJamStatus, String jamToken) {
        long maxDelay = Integer.MAX_VALUE;
        //long maxDelay = 60000;
        Map<String, Object> messageBody = Map.of(
                "jamId", jamId,
                "newJamStatus", newJamStatus,
                "jamToken", jamToken,
                "jamReschedule", (delayInMilliseconds > maxDelay)
        );

        rabbitTemplate.convertAndSend(
                JamStatusRabbitMQConfig.DELAYED_EXCHANGE_NAME,
                JamStatusRabbitMQConfig.ROUTING_KEY,
                messageBody,
                message -> {
                    message.getMessageProperties().setHeader("x-delay", Math.min(delayInMilliseconds, maxDelay));
                    return message;
                }
        );
    }

}
