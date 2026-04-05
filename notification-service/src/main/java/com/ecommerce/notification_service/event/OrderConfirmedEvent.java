package com.ecommerce.notification_service.event;

import java.time.LocalDateTime;
import java.util.List;

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
