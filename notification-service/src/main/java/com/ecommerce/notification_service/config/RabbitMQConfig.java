package com.ecommerce.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // En vez de utilizar serialización java, se utiliza Jackson
    // para convertir objetos a JSON
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public Queue notificationConfirmedQueue() {
        return new Queue("notification-confirmed-queue", true);
    }

    @Bean
    public Queue notificationCancelledQueue() {
        return new Queue("notification-cancelled-queue", true);
    }

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange("order-events");
    }

    @Bean
    public Binding confirmedBinding(Queue notificationConfirmedQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationConfirmedQueue).to(orderEventsExchange).with("order.confirmed");
    }

    @Bean
    public Binding cancelledBinding(Queue notificationCancelledQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationCancelledQueue).to(orderEventsExchange).with("order.cancelled");
    }


}
