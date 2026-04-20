package com.groupsapp.monolito.service;

import com.groupsapp.monolito.model.FileMetadata;
import com.groupsapp.monolito.model.User;
import com.groupsapp.monolito.repository.FileMetadataRepository;
import com.groupsapp.monolito.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileService {

    private final FileMetadataRepository fileRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public FileService(FileMetadataRepository fileRepository,
                       UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FileMetadata uploadFile(MultipartFile file, String userEmail) throws IOException {
        User uploader = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (file.isEmpty()) throw new RuntimeException("El archivo está vacío");
        if (file.getSize() > 20 * 1024 * 1024) throw new RuntimeException("El archivo supera 20MB");

        String originalName = file.getOriginalFilename();
        String storedName   = UUID.randomUUID() + "_" + originalName;
        Path uploadPath     = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(storedName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName(originalName);
        metadata.setStoredName(storedName);
        metadata.setFilePath(filePath.toString());
        metadata.setMimeType(file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setUploadedBy(uploader);
        return fileRepository.save(metadata);
    }

    public Resource downloadFile(Long fileId) throws MalformedURLException {
        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
        Path filePath = Paths.get(metadata.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable())
            throw new RuntimeException("No se puede leer el archivo");
        return resource;
    }

    public FileMetadata getFileMetadata(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
    }
}