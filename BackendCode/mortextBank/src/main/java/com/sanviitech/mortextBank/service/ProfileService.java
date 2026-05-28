package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.KYCUploadRequest;
import com.sanviitech.mortextBank.dto.UpdateProfileRequest;
import com.sanviitech.mortextBank.entity.KYC;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.exception.BadRequestException;
import com.sanviitech.mortextBank.exception.ResourceNotFoundException;
import com.sanviitech.mortextBank.repository.KYCRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    
    private final UserRepository userRepository;
    private final KYCRepository kycRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User updateProfile(UpdateProfileRequest request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        
        return userRepository.save(user);
    }
    
    public User changeSecurityPin(String currentPin, String newPin, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        
        if (!currentPin.equals(user.getSecurityPin())) {
            throw new BadRequestException("Current security pin is incorrect");
        }
        
        user.setSecurityPin(newPin);
        return userRepository.save(user);
    }
    
    public User getProfile(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
    }
    
    public KYC uploadKYC(KYCUploadRequest request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        
        KYC kyc = kycRepository.findByUserId(user.getId()).orElse(new KYC());
        
        kyc.setUser(user);
        kyc.setPanNumber(request.getPanNumber());
        kyc.setAadhaarNumber(request.getAadhaarNumber());
        kyc.setIdProofType(KYC.IdProofType.valueOf(request.getIdProofType()));
        kyc.setIdProofNumber(request.getIdProofNumber());
        kyc.setIdProofImageUrl(request.getIdProofImageUrl());
        kyc.setAddressProofImageUrl(request.getAddressProofImageUrl());
        kyc.setSelfieImageUrl(request.getSelfieImageUrl());
        kyc.setPanCardImageUrl(request.getPanCardImageUrl());
        kyc.setAadhaarCardImageUrl(request.getAadhaarCardImageUrl());
        kyc.setDateOfBirth(request.getDateOfBirth());
        kyc.setPermanentAddress(request.getPermanentAddress());
        kyc.setCurrentAddress(request.getCurrentAddress());
        kyc.setCity(request.getCity());
        kyc.setState(request.getState());
        kyc.setPincode(request.getPincode());
        kyc.setNationality(request.getNationality());
        kyc.setStatus(KYC.KYCStatus.PENDING);
        
        return kycRepository.save(kyc);
    }
    
    public KYC getKYCStatus(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        
        return kycRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("KYC", "userId", user.getId()));
    }
}
