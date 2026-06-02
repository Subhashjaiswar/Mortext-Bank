package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.KYCUploadRequest;
import com.sanviitech.mortextBank.dto.UpdateProfileRequest;
import com.sanviitech.mortextBank.entity.KYC;
import com.sanviitech.mortextBank.entity.User;
import org.springframework.security.core.Authentication;

public interface ProfileService {
    User updateProfile(UpdateProfileRequest request, Authentication authentication);
    User changeSecurityPin(String currentPin, String newPin, Authentication authentication);
    User getProfile(Authentication authentication);
    KYC uploadKYC(KYCUploadRequest request, Authentication authentication);
    KYC getKYCStatus(Authentication authentication);
}