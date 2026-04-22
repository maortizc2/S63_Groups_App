package com.groupsapp.monolito.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Almacenamiento en disco local del servidor.
 * Activo cuando el perfil de Spring es 'local' (desarrollo en laptop).
 */
@Component
@Profile("local")
public class LocalFileStorage implements FileStorage {

    private final Path uploadDir;

    public LocalFileStorage(@Value("${app.upload.dir}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath();
    }

    @Override
    public String store(MultipartFile file, String storedName) throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path target = uploadDir.resolve(storedName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }

    @Override
    public Resource load(String locationKey) throws MalformedURLException {
        Path filePath = Paths.get(locationKey);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("No se puede leer el archivo en disco: " + locationKey);
        }
        return resource;
    }

    @Override
    public void delete(String locationKey) throws IOException {
        Path filePath = Paths.get(locationKey);
        Files.deleteIfExists(filePath);
    }
}