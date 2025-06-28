package com.projetopoo.jam.config.rabbitmq;

import org.springframework.amqp.core.Declarables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe responsável pela fila do RabbitMQ para descompactar os jogos
 */
@Configuration
public class GameExtractRabbitMQConfig {

    public static final String EXCHANGE_NAME = "game-extract-exchange";
    public static final String QUEUE_NAME = "game-extract-queue";
    public static final String ROUTING_KEY = "game.extract";

    @Bean
    public Declarables gameUnzipDeclarables() {
        return RabbitMQComponentBuilder.buildDirectQueueAndBinding(
                EXCHANGE_NAME,
                QUEUE_NAME,
                ROUTING_KEY
        );
    }
}