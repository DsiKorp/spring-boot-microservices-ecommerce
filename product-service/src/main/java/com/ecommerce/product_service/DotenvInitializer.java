package com.ecommerce.product_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DotenvInitializer implements org.springframework.boot.env.EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .load();

        java.util.Map<String, Object> dotenvMap = new java.util.HashMap<>();
        dotenv.entries().forEach(entry ->
            dotenvMap.put(entry.getKey(), entry.getValue())
        );

        environment.getPropertySources().addFirst(
            new MapPropertySource("DOTENV", dotenvMap)
        );
    }
}
