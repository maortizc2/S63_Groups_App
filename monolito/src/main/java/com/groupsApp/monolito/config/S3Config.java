package com.groupsapp.monolito.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Configuración del cliente S3 como bean de Spring.
 * Solo se activa en perfil 'aws'.
 *
 * El DefaultCredentialsProvider busca credenciales en este orden:
 *   1. Variables de entorno (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY).
 *   2. ~/.aws/credentials (en la máquina donde corre).
 *   3. IAM role de la instancia EC2 (endpoint interno de metadata).
 *
 * En EC2 con LabRole asignado, el punto 3 funciona automáticamente
 * sin necesidad de configurar credenciales manualmente.
 */
@Configuration
@Profile("aws")
public class S3Config {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}