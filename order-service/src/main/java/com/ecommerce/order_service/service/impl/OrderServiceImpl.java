package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderLineItems;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
//import org.springframework.web.reactive.function.client.WebClient;
//import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    //private final WebClient.Builder webClientBuilder;
    //private final InventoryClient inventoryClient;
    private final RabbitTemplate rabbitTemplate;
    private final OutboxService outboxService;

    @Value("${orders.enabled:true}")
    private boolean ordersEnabled;

    // CompletableFuture crea asincronismo para ejecutar en un hilo separado
//    public  CompletableFuture<OrderResponse> placeOrderFallback(OrderRequest orderRequest, String userId, Throwable throwable) {
//        return CompletableFuture.supplyAsync(() -> {
//            log.error("\uD83D\uDD34 Circuit Breaker activated. Cause: {}", throwable.getMessage());
//
//            //return new OrderResponse(0L, "00000", Collections.emptyList());
//            throw new RuntimeException("The ordering service is currently undergoing maintenance. Please try again later.");
//        });
//    }

    public OrderResponse placeOrderFallback(OrderRequest orderRequest, String userId, Throwable throwable) {
        log.error("\uD83D\uDD34 Circuit Breaker activated. Cause: {}", throwable.getMessage());
        //return new OrderResponse(0L, "00000", Collections.emptyList());
        throw new RuntimeException("The ordering service is currently undergoing maintenance. Please try again later.");
    }

    @Override
    @Transactional
//    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
//    @Retry(name = "inventory")
    //@TimeLimiter(name = "inventory")
    public OrderResponse placeOrder(OrderRequest orderRequest, String userId) {

        if (!ordersEnabled) {
            log.warn("Order rejected: Service disabled by configuration");
            throw new RuntimeException("The ordering service is currently undergoing maintenance. Please try again later.");
        }

        log.info("Placing new order...");

        // Mapeo manual de items para asegurar la lista
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsList()
                .stream()
                .map(orderMapper::toOrderLineItems)
                .toList();

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PLACED);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderLineItemsList(orderLineItems);
        order.setUserId(userId);

        // se quito por uso de RabbitMQ
//            for (var orderItem : order.getOrderLineItemsList()) {
//                try {
//                    inventoryClient.reduceStock(orderItem.getSku(), orderItem.getQuantity());
//
//                } catch (Exception e) {
//                    log.error("Error reducing inventory, product {}: {}", orderItem.getSku(), e.getMessage());
//                    throw new IllegalArgumentException("Order processing error: " + e.getMessage());
//                }
//            }

        // Guardamos y capturamos la entidad persistida
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved successfully. ID: {}", savedOrder.getId());

        // Crea y Envia el evento a RabbitMQ
        List<OrderPlacedEvent.OrderItemEvent> orderItemsEvents =
                order.getOrderLineItemsList().stream()
                        .map(item -> new OrderPlacedEvent.OrderItemEvent(
                                item.getSku(), item.getPrice().toString(), item.getQuantity()
                        )).toList();

        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(
                savedOrder.getOrderNumber(), orderRequest.getEmail(), savedOrder.getOrderDate(), orderItemsEvents
        );

        boolean sentToRabbit = false;
        try {
            // lo publicamos en el Routing key	 "order.placed" en rabbit, no a la cola
            rabbitTemplate.convertAndSend("order-events", "order.placed", orderPlacedEvent);
            sentToRabbit = true;
            log.info("* Event sent to RabbitMQ for order: {}", savedOrder.getOrderNumber());
        } catch (AmqpException e) {
            log.error("* RabbitMQ error: {} . \nsigue Outbox", e.getMessage());
        }
        outboxService.saveOrderPlacedEvent(orderPlacedEvent, sentToRabbit);

        log.info("Event sent to RabbitMQ for order: {}", savedOrder.getOrderNumber());

        return orderMapper.toOrderResponse(savedOrder);

    }

//    @Override
//    @Transactional
//    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
//    @Retry(name = "inventory")
//    @TimeLimiter(name = "inventory")
//    public CompletableFuture<OrderResponse> placeOrder(OrderRequest orderRequest, String userId) {
//
//        long startTime = System.currentTimeMillis();
//        log.info("Order received. Time: {}", startTime);
//
//        return CompletableFuture.supplyAsync(() -> {
//            if (!ordersEnabled) {
//                log.warn("Order rejected: Service disabled by configuration");
//                throw new RuntimeException("The ordering service is currently undergoing maintenance. Please try again later.");
//            }
//
//            log.info("Placing new order...");
//
//            // Mapeo manual de items para asegurar la lista
//            List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsList()
//                    .stream()
//                    .map(orderMapper::toOrderLineItems)
//                    .toList();
//
//            Order order = new Order();
//            order.setOrderNumber(UUID.randomUUID().toString());
//            order.setOrderLineItemsList(orderLineItems);
//            order.setUserId(userId);
//
//            for (var orderItem : order.getOrderLineItemsList()) {
//
////            Boolean isInStock = webClientBuilder.build()
////                    .get()
////                    .uri("http://localhost:8082/api/v1/inventory/" + orderItem.getSku(), uriBuilder -> uriBuilder
////                            .queryParam("quantity", orderItem.getQuantity())
////                            .build())
////                    .retrieve() // retrieve para obtener la respuesta del servicio de inventario, como en
////                                // postman
////                    .bodyToMono(Boolean.class) // bodyToMono para obtener un solo valor booleano
////                    .block(); // block para esperar la respuesta de forma sincrónica
////
////            if (Boolean.FALSE.equals(isInStock)) {
////                throw new IllegalArgumentException(
////                        "El producto con SKU " + orderItem.getSku() + " no está disponible en stock.");
////            }
//
//                try {

    /// /                String reducerResponse = webClientBuilder.build()
    /// /                        .put()
    /// /                        .uri("http://localhost:8082/api/v1/inventory/reduce/" + orderItem.getSku(), uriBuilder -> uriBuilder
    /// /                                .queryParam("quantity", orderItem.getQuantity())
    /// /                                .build())
    /// /                        .retrieve() // retrieve para obtener la respuesta del servicio de inventario, como en postman
    /// /                        .bodyToMono(String.class) // bodyToMono para obtener un solo valor booleano
    /// /                        .block(); // block para esperar la respuesta de forma sincrónica
    /// /
    /// /                log.info("Inventory reduced for product {}: {}", orderItem.getSku(), reducerResponse);
//
//                    inventoryClient.reduceStock(orderItem.getSku(), orderItem.getQuantity());
//
//                } catch (Exception e) {
//                    log.error("Error reducing inventory, product {}: {}", orderItem.getSku(), e.getMessage());
//                    throw new IllegalArgumentException("Order processing error: " + e.getMessage());
//                }
//
//            }
//
//            long totalTime = System.currentTimeMillis() - startTime;
//            log.info("Order placed. TotalTime: {} ms", totalTime);
//
//            if (totalTime > 3000) {
//                log.error("Order timed out. TotalTime: {} ms", totalTime);
//                throw new RuntimeException("Exceded timed out - Manual Rollback: " + totalTime);
//            }
//
//            // Guardamos y capturamos la entidad persistida
//            Order savedOrder = orderRepository.save(order);
//            log.info("Order saved successfully. ID: {}", savedOrder.getId());
//
//            return orderMapper.toOrderResponse(savedOrder);
//        });
//    }
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(String userId, boolean isAdmin) {
        List<Order> orders;

        if (isAdmin) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByUserId(userId);
        }

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", id));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Orden", "id", id);
        }
        orderRepository.deleteById(id);
        log.info("Order removed. ID: {}", id);
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderNumber, OrderStatus newStatus) {
        log.info("🔄 Actualizando base de datos: Orden {} -> {}", orderNumber, newStatus);

        orderRepository.findByOrderNumber(orderNumber).ifPresentOrElse(
                order -> {
                    order.setStatus(newStatus);
                    orderRepository.save(order);
                    log.info("✅ Estado actualizado en DB para la orden: {} \uD83D\uDCE1", orderNumber);
                },
                () -> log.error("❌ No se encontró la orden {} para actualizar", orderNumber)
        );
    }
}