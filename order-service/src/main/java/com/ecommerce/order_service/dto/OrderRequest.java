package com.ecommerce.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotBlank(message = "The Email is required")
    @Email(message = "The Email is not valid")
    private String email;

    @NotEmpty(message = "The order must contain at least one item")
    @Valid
    private List<OrderLineItemsRequest> orderLineItemsList;
}
