package com.ecommerce.order_service.config;

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

    // En vez de utilizar serialización java, se utiliza Jackson para convertir objetos a JSON
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    // cola para escuchar al inventario y que confirme la orden
    @Bean
    public Queue orderConfirmerdQueue() {
        return new Queue("order-confirmed-queue", true);
    }

    // cola para escuchar al inventario y que cancele la orden
    @Bean
    public Queue orderCancelledQueue() {
        return new Queue("order-cancelled-queue", true);
    }

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange("order-events");
    }

    // conexion cordinada por spring, inyecta la cola y el exchange
    // Binding es como un portero inteligente, le ordena al exchange que solo deje pasar a la
    // cola la orden que le indiquemos con la etiqueta order.confirmed
    @Bean
    public Binding confirmedBinding(Queue orderConfirmerdQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(orderConfirmerdQueue).to(orderEventsExchange).with("order.confirmed");
    }

    @Bean
    public Binding cancelledBinding(Queue orderCancelledQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(orderCancelledQueue).to(orderEventsExchange).with("order.cancelled");
    }


}
