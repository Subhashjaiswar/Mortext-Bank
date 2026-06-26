package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.AuthResponse;
import com.sanviitech.mortextBank.dto.ForgotPasswordRequest;
import com.sanviitech.mortextBank.dto.LoginRequest;
import com.sanviitech.mortextBank.dto.OTPVerificationRequest;
import com.sanviitech.mortextBank.dto.RegisterRequest;
import com.sanviitech.mortextBank.dto.ResetPasswordRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void verifyOTP(OTPVerificationRequest request);
}