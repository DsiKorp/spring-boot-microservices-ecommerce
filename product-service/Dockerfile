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

# docker compose up --build
# docker compose up

# -d para que corra en segundo plano
# docker compose up -d

# levanta solamente mongodb
# docker compose up -d mongodb

# down para y elimina los contenedores
# docker compose down
# docker image ls

# -v elimina los volumenes asociados a docker compose,
# borra la base de products
# docker compose down -v

# docker image ls

# run proyect:
# ./mvnw spring-boot:run
