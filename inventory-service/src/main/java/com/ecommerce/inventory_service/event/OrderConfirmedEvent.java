package com.ecommerce.inventory_service.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderConfirmedEvent(
        String orderNumber,
        String email,
        LocalDateTime orderDate,
        List<OrderItemEvent> items
) {
    public record OrderItemEvent(
            String sku,
            String price,
            Integer quantity
    ) {}
}
