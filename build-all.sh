#!/bin/bash

# Script para compilar todos los microservicios
# Ejecutar desde la carpeta spring-boot-microservices-ecommerce

MICROSERVICES=(
    "api-gateway"
    "config-server"
    "discovery-server"
    "inventory-service"
    "notification-service"
    "order-service"
    "product-service"
)

echo "=========================================="
echo "Compilando todos los microservicios..."
echo "=========================================="

for service in "${MICROSERVICES[@]}"; do
    echo ""
    echo ">>> Compilando: $service"
    echo "------------------------------------------"

    if [ -d "$service" ]; then
        cd "$service"
        ./mvnw clean install -DskipTests
        cd ..
        echo ">>> $service compilado correctamente"
    else
        echo ">>> ERROR: Directorio $service no encontrado"
    fi
done

echo ""
echo "=========================================="
echo "Compilacion completada!"
echo "=========================================="