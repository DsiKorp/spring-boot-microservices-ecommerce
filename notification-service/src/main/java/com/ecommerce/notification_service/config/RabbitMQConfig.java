package com.ecommerce.notification_service.config;

import com.ecommerce.notification_service.event.OrderCancelledEvent;
import com.ecommerce.notification_service.event.OrderConfirmedEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // En vez de utilizar serialización java, se utiliza Jackson
    // para convertir objetos a JSON
//    @Bean
//    public MessageConverter messageConverter() {
//        return new JacksonJsonMessageConverter();
//    }

    @Bean
    public MessageConverter messageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();

        // Confiamos en los paquetes para evitar bloqueos de seguridad
        classMapper.setTrustedPackages("*");

        // MAPEAMOS LAS IDENTIDADES (Basado en tus logs reales)
        Map<String, Class<?>> idClassMapping = new HashMap<>();

        // Tus logs dicen que el Inventario manda 'OrderPlacedEvent' cuando confirma
        idClassMapping.put("com.ecommerce.inventory_service.event.OrderConfirmedEvent", OrderConfirmedEvent.class);

        // Y manda 'OrderCancelledEvent' cuando falla
        idClassMapping.put("com.ecommerce.inventory_service.event.OrderCancelledEvent", OrderCancelledEvent.class);

        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper); // Usamos el ClassMapper moderno

        return converter;
    }

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange("order-events");
    }

//    @Bean
//    public Queue notificationConfirmedQueue() {
//        return new Queue("notification-confirmed-queue", true);
//    }

    @Bean
    public Queue notificationConfirmedQueue() {
        return QueueBuilder.durable("notification-confirmed-queue")
                .withArgument("x-dead-letter-exchange", "notification-dlx")
                .withArgument("x-dead-letter-routing-key", "notification.dead")
                .build();
    }

//    @Bean
//    public Queue notificationCancelledQueue() {
//        return new Queue("notification-cancelled-queue", true);
//    }

    @Bean
    public Queue notificationCancelledQueue() {
        return QueueBuilder.durable("notification-cancelled-queue")
                .withArgument("x-dead-letter-exchange", "notification-dlx")
                .withArgument("x-dead-letter-routing-key", "notification.dead")
                .build();
    }

    @Bean
    public Binding confirmedBinding(Queue notificationConfirmedQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationConfirmedQueue).to(orderEventsExchange).with("order.confirmed");
    }

    @Bean
    public Binding cancelledBinding(Queue notificationCancelledQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationCancelledQueue).to(orderEventsExchange).with("order.cancelled");
    }

    /// //////////////////////////////////////////////////////////////////////////////////////////////////
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("notification-dlx");
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("notification-dlq", true);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("notification.dead");
    }

}
