package com.groupsapp.monolito.controller;

import com.groupsapp.monolito.dto.ApiResponse;
import com.groupsapp.monolito.model.FileMetadata;
import com.groupsapp.monolito.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileMetadata>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        FileMetadata metadata = fileService.uploadFile(file, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Archivo subido exitosamente", metadata));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws Exception {
        FileMetadata metadata = fileService.getFileMetadata(id);
        Resource resource     = fileService.downloadFile(id);
        String contentDisposition = metadata.isImage()
                ? "inline; filename=\"" + metadata.getOriginalName() + "\""
                : "attachment; filename=\"" + metadata.getOriginalName() + "\"";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<ApiResponse<FileMetadata>> getFileInfo(@PathVariable Long id) {
        FileMetadata metadata = fileService.getFileMetadata(id);
        return ResponseEntity.ok(ApiResponse.ok("Metadatos obtenidos", metadata));
    }
}