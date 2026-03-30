package com.ecommerce.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @NotEmpty(message = "The order must contain at least one item")
    @Valid
    private List<OrderLineItemsRequest> orderLineItemsDtoList;
}
