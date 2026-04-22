package com.groupsapp.monolito.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

/**
 * Almacenamiento en Amazon S3.
 * Activo cuando el perfil de Spring es 'aws' (despliegue en EC2).
 */
@Component
@Profile("aws")
public class S3FileStorage implements FileStorage {

    private final S3Client s3Client;
    private final String bucketName;

    public S3FileStorage(S3Client s3Client,
                        @Value("${app.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String store(MultipartFile file, String storedName) throws IOException {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(storedName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // En S3 la "ubicación" es simplemente la key dentro del bucket.
        return storedName;
    }

    @Override
    public Resource load(String locationKey) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(locationKey)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getRequest);
        return new InputStreamResource(s3Object);
    }

    @Override
    public void delete(String locationKey) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(locationKey)
                .build();
        s3Client.deleteObject(deleteRequest);
    }
}