package com.ecommerce.inventory_service.listener;

import com.ecommerce.inventory_service.event.OrderCancelledEvent;
import com.ecommerce.inventory_service.event.OrderPlacedEvent;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class OrderEventsListener {

    private final InventoryService inventoryService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "inventory-queue")
    public void handleOrderPlacedEvent(OrderPlacedEvent orderPlacedEvent) {
        log.info("OrderPlacedEvent received: {}", orderPlacedEvent);
        log.info("Event received in Inventory for order: {}", orderPlacedEvent.orderNumber());

            try {
                boolean allProductsInStock = orderPlacedEvent.items().stream()
                                .allMatch(item -> inventoryService.isInStock(item.sku(), item.quantity()));

                if (!allProductsInStock) {
                    cancelOrder(orderPlacedEvent, "Not enough stock for all items");
                    return;
                }

                orderPlacedEvent.items().forEach(item -> {
                    inventoryService.reduceStock(item.sku(), item.quantity());
                    log.info("Stock reduced for SKU: {} by {}", item.sku(), item.quantity());
                });

                rabbitTemplate.convertAndSend("order-events", "order.confirmed", orderPlacedEvent);


            } catch (Exception e) {
                log.error("Error processing inventory: {}", e.getMessage());
                cancelOrder(orderPlacedEvent, "Error processing inventory: " + e.getMessage());
            }
    }

//    @RabbitListener(queues = "inventory-queue")
//    public void handleOrderPlacedEvent(OrderPlacedEvent orderPlacedEvent) {
//        log.info("OrderPlacedEvent received: {}", orderPlacedEvent);
//        log.info("Event received in Inventory for order received: {}", orderPlacedEvent.orderNumber());
//
//        orderPlacedEvent.items().forEach( item -> {
//            try {
//                inventoryService.reduceStock(item.sku(), item.quantity());
//                log.info("Stock reduced for SKU: {} by {}", item.sku(), item.quantity());
//            } catch (Exception e) {
//                log.error("Error reducing stock for SKU: {}: {}", item.sku(), e.getMessage());
//            }
//        });
//    }

    private void cancelOrder(OrderPlacedEvent orderPlacedEvent, String reason) {
        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(
                orderPlacedEvent.orderNumber(),
                orderPlacedEvent.email(),
                reason
        );
        log.info("OrderCancelledEvent sent: {}", orderCancelledEvent);
        rabbitTemplate.convertAndSend("order-events", "order.cancelled", orderCancelledEvent);
    }
}
