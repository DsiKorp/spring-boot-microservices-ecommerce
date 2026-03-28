package com.ecommerce.product_service.config;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

// le dice a spring que no use use la autoconfiguracion
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    // Inyectamos las variables.
    // La sintaxis es: "${NOMBRE_VARIABLE_EN_SPRING}"
    @Value("${MONGO_HOST}")
    private String host;

    @Value("${MONGO_PORT}")
    private int port;

    @Value("${MONGO_DATABASE}")
    private String database;

    @Value("${MONGO_USERNAME}")
    private String username;

    @Value("${MONGO_PASSWORD}")
    private String password;

    @Value("${MONGO_AUTH_DB}")
    private String authDatabase;


    @Override
    protected String getDatabaseName() {
        return "product-db";
    }

    @Override
    @Bean
    public MongoClient mongoClient(){
        // Usamos las variables inyectadas en lugar de texto fijo
        MongoCredential credential = MongoCredential.createCredential(
                username, authDatabase, password.toCharArray()
        );

        // Agregamos un string format para armar la URL dinámicamente
        String connectionString = String.format("mongodb://%s:%d", host, port);

        MongoClientSettings mongoClientSettings = MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .credential(credential)
                .build();

        return MongoClients.create(mongoClientSettings);
    }
}
