package com.ecommerce.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryRequestDto(

        @NotBlank(message = "The sku cannot be empty")
        String sku,

        @Min(value = 0, message = "The quantity must be greater than or equal to 0")
        Integer quantity
) {
}
