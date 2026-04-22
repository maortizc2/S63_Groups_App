package com.groupsapp.monolito.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Contrato (abstracción) para almacenamiento de archivos.
 * Implementaciones concretas:
 *   - LocalFileStorage (disco del servidor)
 *   - S3FileStorage   (Amazon S3)
 *
 * FileService depende de esta interfaz, no de sus implementaciones.
 * Spring Boot inyecta la implementación apropiada según el perfil activo.
 */
public interface FileStorage {

    /**
     * Guarda un archivo y devuelve la "ubicación" (key) donde quedó.
     * La key es opaca desde el punto de vista del caller:
     *   - En Local:  ruta absoluta del archivo.
     *   - En S3:     key dentro del bucket (ej: "messages/uuid-foto.jpg").
     * Esa key es lo que se persiste en FileMetadata.filePath.
     */
    String store(MultipartFile file, String storedName) throws IOException;

    /**
     * Recupera un archivo a partir de su key/ubicación.
     * Devuelve un Resource de Spring (abstracción sobre InputStream).
     */
    Resource load(String locationKey) throws IOException;

    /**
     * Borra un archivo. Útil cuando un usuario elimina un mensaje con adjunto.
     * (No se llama todavía en el código, lo dejamos preparado.)
     */
    void delete(String locationKey) throws IOException;
}