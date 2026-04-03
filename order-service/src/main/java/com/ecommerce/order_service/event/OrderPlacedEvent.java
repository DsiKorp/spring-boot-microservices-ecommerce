package com.ecommerce.order_service.event;

import java.util.List;

// Alta cohesión y atomicidad
public record OrderPlacedEvent(
        String orderNumber,
        String email,
        List<OrderItemEvent> items
) {
    public record OrderItemEvent(
            String sku,
            String price,
            Integer quantity
    ) {}

    // --- MÉTODO MAIN DE PRUEBA (SOLO PARA APRENDER) ---
    public static void main(String[] args) {

        // 1. Crear los items (Usando el constructor del record interno)
        // Fíjate que se accede como: OrderPlacedEvent.OrderItemEvent
        var item1 = new OrderPlacedEvent.OrderItemEvent("iphone-15", "1200.00", 1);
        var item2 = new OrderPlacedEvent.OrderItemEvent("macbook-pro", "2500.00", 2);

        // 2. Crear el evento principal con la lista de items
        var evento = new OrderPlacedEvent("ORD-12345", "gabriel@test.com",
                List.of(item1, item2));

        // 3. Imprimir (Los records tienen un toString() hermoso por defecto)
        System.out.println("--- EVENTO CREADO ---");
        System.out.println(evento);

        // 4. Simular cómo lo leería el Inventario
        System.out.println("\n--- LECTURA (SIMULANDO INVENTARIO) ---");
        for (OrderItemEvent item : evento.items()) {
            System.out.println("Descontando stock: " + item.sku() + " - Cantidad: " + item.quantity());
        }
    }
}
