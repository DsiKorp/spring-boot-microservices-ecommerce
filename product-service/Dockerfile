# --- ETAPA 1: BUILD (La Construcción) ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
# RUN se usa para armar la casa y ENTRYPOINT para vivir en ella
# --- ETAPA 2: RUNTIME (El Producto Final) ---
# Entorno de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar product.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "product.jar"]

# docker compose up
# docker compose up -d
# docker compose up -d mongodb
# docker compose down
# docker compose down -v
# docker image ls

# ./mvnw spring-boot:run
