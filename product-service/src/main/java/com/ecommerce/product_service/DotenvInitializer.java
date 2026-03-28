package com.ecommerce.product_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DotenvInitializer implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Busca el .env en el working directory actual o en subdirectorios comunes
        String[] possibleDirs = {".", "product-service"};

        Dotenv dotenv = null;
        for (String dir : possibleDirs) {
            try {
                dotenv = Dotenv.configure()
                        .directory(dir)
                        .ignoreIfMissing()
                        .load();
                if (dotenv.entries().iterator().hasNext()) break;
            } catch (Exception ignored) {}
        }

        if (dotenv == null) return;

        java.util.Map<String, Object> dotenvMap = new java.util.HashMap<>();
        dotenv.entries().forEach(entry ->
                dotenvMap.put(entry.getKey(), entry.getValue())
        );

        environment.getPropertySources().addFirst(
                new MapPropertySource("DOTENV", dotenvMap)
        );
    }
}
