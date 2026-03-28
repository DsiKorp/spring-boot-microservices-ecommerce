package com.ecommerce.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "The product name cannot be empty")
        String name,

        String description, // Este es opcional, lo dejamos sin validación

        @NotNull(message = "The price is mandatory")
        @Positive(message = "The price must be greater than zero")
        BigDecimal price
) {
}
