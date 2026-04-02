package com.ecommerce.order_service.config;

import com.ecommerce.order_service.service.client.InventoryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;


/*
    para ip y puerto fijo del .evv
   @Bean
    public WebClient webClientBuilder() {
        return WebClient.builder().baseUrl(inventoryServiceUrl).build();
    }
*/

    // para eureka ip y puerto dinámico
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    InventoryClient inventoryClient(WebClient.Builder builder) {
        WebClient webClient = builder.baseUrl(inventoryServiceUrl).build();

        // crear un adaptador para poder usar WebClient como cliente de servicio
        // WebClientAdapter es el que sabe como ejecutar la petición utilizando webClient
        WebClientAdapter adapter = WebClientAdapter.create(webClient);

        // La fabrica de servicios HTTP, HttpServiceProxyFactory crea la clase de implentación de InventoryClient que es una interface
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        // Crear el cliente de servicio InventoryClient.class
        return factory.createClient(InventoryClient.class);
    }
}
