package com.ecommerce.notification_service.event;

import lombok.Builder;

@Builder
public record OrderCancelledEvent(
        String orderNumber,
        String email,
        String reason
) {
}
