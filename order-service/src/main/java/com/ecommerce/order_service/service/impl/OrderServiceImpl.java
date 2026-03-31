package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderLineItems;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.client.InventoryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    //private final WebClient.Builder webClientBuilder;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        log.info("Placing new order...");

        // Mapeo manual de items para asegurar la lista
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsList()
                .stream()
                .map(orderMapper::toOrderLineItems)
                .toList();

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(orderLineItems);

        for (var orderItem : order.getOrderLineItemsList()) {

//            Boolean isInStock = webClientBuilder.build()
//                    .get()
//                    .uri("http://localhost:8082/api/v1/inventory/" + orderItem.getSku(), uriBuilder -> uriBuilder
//                            .queryParam("quantity", orderItem.getQuantity())
//                            .build())
//                    .retrieve() // retrieve para obtener la respuesta del servicio de inventario, como en
//                                // postman
//                    .bodyToMono(Boolean.class) // bodyToMono para obtener un solo valor booleano
//                    .block(); // block para esperar la respuesta de forma sincrónica
//
//            if (Boolean.FALSE.equals(isInStock)) {
//                throw new IllegalArgumentException(
//                        "El producto con SKU " + orderItem.getSku() + " no está disponible en stock.");
//            }

            try {
//                String reducerResponse = webClientBuilder.build()
//                        .put()
//                        .uri("http://localhost:8082/api/v1/inventory/reduce/" + orderItem.getSku(), uriBuilder -> uriBuilder
//                                .queryParam("quantity", orderItem.getQuantity())
//                                .build())
//                        .retrieve() // retrieve para obtener la respuesta del servicio de inventario, como en postman
//                        .bodyToMono(String.class) // bodyToMono para obtener un solo valor booleano
//                        .block(); // block para esperar la respuesta de forma sincrónica
//
//                log.info("Inventory reduced for product {}: {}", orderItem.getSku(), reducerResponse);

                inventoryClient.reduceStock(orderItem.getSku(), orderItem.getQuantity());

            } catch (Exception e) {
                log.error("Error reducing inventory, product {}: {}", orderItem.getSku(), e.getMessage());
                throw new IllegalArgumentException("Order processing error: " + e.getMessage());
            }

        }

        // Guardamos y capturamos la entidad persistida
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved successfully. ID: {}", savedOrder.getId());

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
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
}