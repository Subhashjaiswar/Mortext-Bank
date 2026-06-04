package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.dto.KYCResponse;
import com.sanviitech.mortextBank.dto.KYCUploadRequest;
import com.sanviitech.mortextBank.dto.UpdateProfileRequest;
import com.sanviitech.mortextBank.dto.UserResponse;
import com.sanviitech.mortextBank.entity.KYC;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.constants.ValidationConstants;
import com.sanviitech.mortextBank.util.GlobalException;
import com.sanviitech.mortextBank.mapper.KYCMapper;
import com.sanviitech.mortextBank.mapper.UserMapper;
import com.sanviitech.mortextBank.repository.KYCRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import com.sanviitech.mortextBank.service.ProfileService;
import com.sanviitech.mortextBank.validator.EmailValidator;
import com.sanviitech.mortextBank.validator.NameValidator;
import com.sanviitech.mortextBank.validator.SecurityPinValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final KYCRepository kycRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final KYCMapper kycMapper;

    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private NameValidator nameValidator;

    @Autowired
    private SecurityPinValidator securityPinValidator;

    @Override
    public UserResponse updateProfile(UpdateProfileRequest request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

        if (request.getFullName() != null) {
            String nameError = nameValidator.validate(request.getFullName(), "Full name");
            if (nameError != null) {
                throw GlobalException.badRequest(nameError);
            }
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            String emailError = emailValidator.validate(request.getEmail());
            if (emailError != null) {
                throw GlobalException.badRequest(emailError);
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw GlobalException.badRequest(ValidationConstants.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse changeSecurityPin(String currentPin, String newPin, Authentication authentication) {
        String currentPinError = securityPinValidator.validate(currentPin);
        if (currentPinError != null) {
            throw GlobalException.badRequest(currentPinError);
        }

        String newPinError = securityPinValidator.validate(newPin);
        if (newPinError != null) {
            throw GlobalException.badRequest(newPinError);
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

        if (!currentPin.equals(user.getSecurityPin())) {
            throw GlobalException.badRequest(ValidationConstants.CURRENT_SECURITY_PIN_INCORRECT);
        }

        user.setSecurityPin(newPin);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getProfile(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));
        return userMapper.toResponse(user);
    }

    @Override
    public KYCResponse uploadKYC(KYCUploadRequest request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

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

        KYC savedKYC = kycRepository.save(kyc);
        return kycMapper.toResponse(savedKYC);
    }

    @Override
    public KYCResponse getKYCStatus(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

        KYC kyc = kycRepository.findByUserId(user.getId())
                .orElseThrow(() -> GlobalException.resourceNotFound("KYC", "userId", user.getId()));
        return kycMapper.toResponse(kyc);
    }
}