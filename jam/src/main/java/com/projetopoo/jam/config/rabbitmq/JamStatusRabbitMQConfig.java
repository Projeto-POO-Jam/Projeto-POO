package com.projetopoo.jam.config.rabbitmq;

import org.springframework.amqp.core.Declarables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JamStatusRabbitMQConfig {

    public static final String DELAYED_EXCHANGE_NAME = "jam-delayed-exchange";
    public static final String QUEUE_NAME = "jam-status-update-queue";
    public static final String ROUTING_KEY = "jam.status.update";

    @Bean
    public Declarables jamStatusDeclarables() {
        return RabbitMQComponentBuilder.buildDelayedQueueAndBinding(
                DELAYED_EXCHANGE_NAME,
                QUEUE_NAME,
                ROUTING_KEY
        );
    }
}