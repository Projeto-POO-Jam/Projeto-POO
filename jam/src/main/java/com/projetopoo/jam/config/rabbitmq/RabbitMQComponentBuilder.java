package com.projetopoo.jam.config.rabbitmq;

import org.springframework.amqp.core.*;

import java.util.HashMap;
import java.util.Map;

public class RabbitMQComponentBuilder {

    public static Declarables buildDirectQueueAndBinding(String exchangeName, String queueName, String routingKey) {
        DirectExchange exchange = new DirectExchange(exchangeName);
        Queue queue = new Queue(queueName, true);
        Binding binding = BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey);

        return new Declarables(queue, exchange, binding);
    }

    public static Declarables buildDelayedQueueAndBinding(String exchangeName, String queueName, String routingKey) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        CustomExchange exchange = new CustomExchange(exchangeName, "x-delayed-message", true, false, args);

        Queue queue = new Queue(queueName, true);

        Binding binding = BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey)
                .noargs();

        return new Declarables(queue, exchange, binding);
    }
}