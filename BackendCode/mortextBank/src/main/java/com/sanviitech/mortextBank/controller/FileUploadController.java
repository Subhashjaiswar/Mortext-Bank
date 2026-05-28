package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "File Upload APIs")
public class FileUploadController {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @PostMapping("/image")
    @Operation(summary = "Upload image file")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath);
            
            String fileUrl = "/uploads/" + fileName;
            
            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("fileUrl", fileUrl);
            
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        }
    }
    
    @PostMapping("/document")
    @Operation(summary = "Upload document file")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath);
            
            String fileUrl = "/uploads/" + fileName;
            
            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("fileUrl", fileUrl);
            
            return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", response));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to upload document: " + e.getMessage()));
        }
    }
}
