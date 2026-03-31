package com.ecommerce.order_service.service.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PutExchange;

//
public interface InventoryClient {

    // El InventoryClient es solo una definición, es como un request de postman que no hace nada por sí sola.
    // Necesita el click para poder funcionar y para eso es WebClientConfig.java
    // PutExchange ruta del lado del cliente, @PutMapping es una ruta del lado del servidor
    // PutMapping es una ruta que esta escuhando, PutExchange es el que llama a esa ruta
    @PutExchange("/api/v1/inventory/reduce/{sku}")
    String reduceStock(@PathVariable String sku, @RequestParam Integer quantity);
}
