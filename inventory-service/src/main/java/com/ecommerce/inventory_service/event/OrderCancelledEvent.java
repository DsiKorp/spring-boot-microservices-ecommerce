package com.ecommerce.inventory_service.event;

import lombok.Builder;

@Builder
public record OrderCancelledEvent(
        String orderNumber,
        String email,
        String reason
) {
}
