package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.KYCResponse;
import com.sanviitech.mortextBank.dto.KYCUploadRequest;
import com.sanviitech.mortextBank.dto.UpdateProfileRequest;
import com.sanviitech.mortextBank.dto.UserResponse;
import org.springframework.security.core.Authentication;

public interface ProfileService {
    UserResponse updateProfile(UpdateProfileRequest request, Authentication authentication);
    UserResponse changeSecurityPin(String currentPin, String newPin, Authentication authentication);
    UserResponse getProfile(Authentication authentication);
    KYCResponse uploadKYC(KYCUploadRequest request, Authentication authentication);
    KYCResponse getKYCStatus(Authentication authentication);
}