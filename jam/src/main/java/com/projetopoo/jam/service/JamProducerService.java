package com.projetopoo.jam.service;

import com.projetopoo.jam.config.rabbitmq.JamStatusRabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Classe responsável por agendar jams no RabbitMQ para mudar de status
 */
@Service
public class JamProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public JamProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void scheduleJamStatusUpdate(Long jamId, long delayInMilliseconds, String newJamStatus, String jamToken) {
        // Limite máximo de tempo de agendamento permitido, caso necessite de mais tempo deve ter um reagendamento
        long maxDelay = Integer.MAX_VALUE;
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
