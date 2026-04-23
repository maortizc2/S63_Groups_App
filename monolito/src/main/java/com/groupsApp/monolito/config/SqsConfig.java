package com.groupsapp.monolito.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

/**
 * Configuración del cliente SQS.
 *
 * A diferencia de S3Config, este bean se registra en TODOS los perfiles
 * (tanto 'local' con LocalStack como 'aws' con SQS real), porque SQS
 * se usa en ambos ambientes. La diferencia es solo el endpoint:
 *
 *   local: http://localstack:4566
 *   aws:   (vacío, SDK usa el endpoint regional por defecto)
 *
 * El endpoint se inyecta por variable de entorno o propiedad.
 */
@Configuration
public class SqsConfig {

    @Bean
    public SqsClient sqsClient(
            @Value("${app.sqs.endpoint:}") String endpoint,
            @Value("${app.sqs.region:us-east-1}") String region) {

            var builder = SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create());

        // Si hay endpoint custom (LocalStack), lo usamos.
        // Si está vacío, el SDK usa el endpoint regional oficial de AWS.
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }
}