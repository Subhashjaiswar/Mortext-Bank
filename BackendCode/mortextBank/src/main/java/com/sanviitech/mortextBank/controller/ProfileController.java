package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.ApiResponse;
import com.sanviitech.mortextBank.dto.KYCUploadRequest;
import com.sanviitech.mortextBank.dto.UpdateProfileRequest;
import com.sanviitech.mortextBank.entity.KYC;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile & Settings", description = "Profile & Settings APIs")
public class ProfileController {
    
    private final ProfileService profileService;
    
    @GetMapping
    @Operation(summary = "Get user profile")
    public ResponseEntity<ApiResponse<User>> getProfile(Authentication authentication) {
        User user = profileService.getProfile(authentication);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", user));
    }
    
    @PutMapping
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(@Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {
        User user = profileService.updateProfile(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
    }
    
    @PostMapping("/kyc")
    @Operation(summary = "Upload KYC documents")
    public ResponseEntity<ApiResponse<KYC>> uploadKYC(@Valid @RequestBody KYCUploadRequest request, Authentication authentication) {
        KYC kyc = profileService.uploadKYC(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("KYC documents uploaded successfully", kyc));
    }
    
    @GetMapping("/kyc")
    @Operation(summary = "Get KYC status")
    public ResponseEntity<ApiResponse<KYC>> getKYCStatus(Authentication authentication) {
        KYC kyc = profileService.getKYCStatus(authentication);
        return ResponseEntity.ok(ApiResponse.success("KYC status retrieved successfully", kyc));
    }
}
