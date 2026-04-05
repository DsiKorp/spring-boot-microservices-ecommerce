package com.ecommerce.notification_service.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderCancelledEvent(
        String orderNumber,
        String email,
        LocalDateTime orderDate,
        String reason
) {
}
