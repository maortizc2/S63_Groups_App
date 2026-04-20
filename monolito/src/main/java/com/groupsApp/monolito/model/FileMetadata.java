package com.groupsapp.monolito.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "files_metadata")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre original del archivo (ej: "foto_vacaciones.jpg")
    @Column(nullable = false)
    private String originalName;

    // Nombre único en disco (ej: "uuid-1234-foto_vacaciones.jpg")
    @Column(nullable = false, unique = true)
    private String storedName;

    // Ruta donde está guardado en el servidor (o key en S3)
    @Column(nullable = false)
    private String filePath;

    // Tipo MIME (ej: "image/jpeg", "application/pdf")
    @Column(nullable = false, length = 100)
    private String mimeType;

    // Tamaño en bytes
    @Column(nullable = false)
    private Long size;

    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    // ── Quién subió el archivo ────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    // ── Hook ──────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    // ── Métodos de utilidad ───────────────────────────
    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public String getSizeFormatted() {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return (size / 1024) + " KB";
        return (size / (1024 * 1024)) + " MB";
    }

    // ── Getters y Setters ─────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
}