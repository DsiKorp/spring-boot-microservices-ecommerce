package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventsListener {

    @RabbitListener(queues = "notification-queue")
    public void handleOrderPlacedEvent(OrderPlacedEvent orderPlacedEvent) {
        log.info("OrderPlacedEvent received: {}", orderPlacedEvent);
        log.info("Event received in Notification for order received: {}", orderPlacedEvent.orderNumber());

        orderPlacedEvent.items().forEach( item -> {
            try {
                 log.info("✅\u200B Sending confirm email to: {}, Order {}", orderPlacedEvent.email(), orderPlacedEvent.orderNumber());

                 log.info("✅\u200B email to: {}, was sent successfully", orderPlacedEvent.email());
            } catch (Exception e) {
                log.error("❌\u200B Error sending email: {}: {}", item.sku(), e.getMessage());
            }
        });
    }
}
