package com.ecommerce.order_service.scheduler;

import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.model.OutboxEvent;
import com.ecommerce.order_service.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageRelayer {
    private final RabbitTemplate rabbitTemplate;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 10000)
    public void relayMessage() {

        List<OutboxEvent> pendingEvents = outboxService.getPendingEvents();

        if (pendingEvents.isEmpty()) return;

        log.info("* Relayer: Detected {} pending messages", pendingEvents.size());

        pendingEvents.forEach(event -> {
            try {
                OrderPlacedEvent originalEvent = objectMapper.readValue(
                        event.getPayload(), OrderPlacedEvent.class
                );

                rabbitTemplate.convertAndSend("order-events", "order.placed", originalEvent);
                outboxService.markAsProcessed(event.getId());
                log.info("* Relayer: Processed and marked as processed: {}", event.getId());
                log.info("* Relayer: Processed and marked as processed: {}", event.getAggregateId());
            } catch (JacksonException e) {
                log.error("* Relayer: Error deserializing event: {}\n{}", event.getId(), e.getMessage());
            } catch (AmqpException e) {
                log.error("* Relayer: Error sending retry event: {}\n{}", event.getAggregateId(), e.getMessage());
            }
        });
    }
}
