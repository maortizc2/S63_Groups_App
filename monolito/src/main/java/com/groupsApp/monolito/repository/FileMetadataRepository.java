package com.groupsapp.monolito.repository;

import com.groupsapp.monolito.model.FileMetadata;
import com.groupsapp.monolito.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    // Todos los archivos subidos por un usuario
    List<FileMetadata> findByUploadedBy(User user);

    // Buscar archivo por nombre único en disco
    Optional<FileMetadata> findByStoredName(String storedName);

    // Archivos por tipo MIME (ej: todas las imágenes)
    List<FileMetadata> findByMimeTypeStartingWith(String mimeTypePrefix);
}