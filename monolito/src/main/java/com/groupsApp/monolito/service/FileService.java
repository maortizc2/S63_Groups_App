package com.groupsapp.monolito.service;

import com.groupsapp.monolito.model.FileMetadata;
import com.groupsapp.monolito.model.User;
import com.groupsapp.monolito.repository.FileMetadataRepository;
import com.groupsapp.monolito.repository.UserRepository;
import com.groupsapp.monolito.storage.FileStorage;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    private final FileMetadataRepository fileRepository;
    private final UserRepository userRepository;
    private final FileStorage fileStorage;

    public FileService(FileMetadataRepository fileRepository,
                        UserRepository userRepository,
                        FileStorage fileStorage) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.fileStorage = fileStorage;
    }

    @Transactional
    public FileMetadata uploadFile(MultipartFile file, String userEmail) throws IOException {
        User uploader = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }
        if (file.getSize() > 20 * 1024 * 1024) {
            throw new RuntimeException("El archivo supera 20MB");
        }

        String originalName = file.getOriginalFilename();
        String storedName   = UUID.randomUUID() + "_" + originalName;

        // Delegamos el "cómo guardar" a la implementación activa de FileStorage.
        String locationKey = fileStorage.store(file, storedName);

        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName(originalName);
        metadata.setStoredName(storedName);
        metadata.setFilePath(locationKey);
        metadata.setMimeType(file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setUploadedBy(uploader);
        return fileRepository.save(metadata);
    }

    public Resource downloadFile(Long fileId) throws IOException {
        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
        return fileStorage.load(metadata.getFilePath());
    }

    public FileMetadata getFileMetadata(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
    }
}