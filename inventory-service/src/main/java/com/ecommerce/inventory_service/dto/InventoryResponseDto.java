package com.ecommerce.inventory_service.dto;

public record InventoryResponseDto(
        Long id,
        String sku,
        Integer quantity,

        // Calculated field
        boolean inStock
) {
}
