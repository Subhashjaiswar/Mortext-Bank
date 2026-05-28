package com.sanviitech.mortextBank.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sanviitech.mortextBank.dto.AuthResponse;
import com.sanviitech.mortextBank.dto.ForgotPasswordRequest;
import com.sanviitech.mortextBank.dto.LoginRequest;
import com.sanviitech.mortextBank.dto.OTPVerificationRequest;
import com.sanviitech.mortextBank.dto.RegisterRequest;
import com.sanviitech.mortextBank.dto.ResetPasswordRequest;
import com.sanviitech.mortextBank.entity.OTP;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.exception.BadRequestException;
import com.sanviitech.mortextBank.exception.ResourceNotFoundException;
import com.sanviitech.mortextBank.repository.AccountRepository;
import com.sanviitech.mortextBank.repository.OTPRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import com.sanviitech.mortextBank.security.JwtTokenProvider;
import com.sanviitech.mortextBank.util.EmailService;
import com.sanviitech.mortextBank.util.OTPUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OTPRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setSecurityPin(request.getSecurityPin());
        
        user = userRepository.save(user);
        
        String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(user.getEmail(), null, null));
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getFullName(), user.getEmail(), "CUSTOMER", null);
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        
        if (!user.getSecurityPin().equals(request.getSecurityPin())) {
            throw new BadRequestException("Invalid security pin");
        }
        
        String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(user.getEmail(), null, null));
        
        String accountNumber = accountRepository.findByUserId(user.getId())
                .stream()
                .findFirst()
                .map(com.sanviitech.mortextBank.entity.Account::getAccountNumber)
                .orElse(null);
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getFullName(), user.getEmail(), "CUSTOMER", accountNumber);
    }
    
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        String otp = OTPUtil.generateOTP();
        saveOTP(user.getEmail(), "", otp, OTP.OTPType.FORGOT_PASSWORD);
        emailService.sendOTPEmail(user.getEmail(), otp, "Security Pin Reset");
    }

//    public void forgotPassword(ForgotPasswordRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
//
//        String otp = OTPUtil.generateOTP();
//        saveOTP(user.getEmail(), otp, OTP.OTPType.FORGOT_PASSWORD);
//        emailService.sendOTPEmail(user.getEmail(), otp, "Password Reset");
//    }
    
    public void resetPassword(ResetPasswordRequest request) {
        OTP otp = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));
        
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }
        
        User user = userRepository.findByEmail(otp.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", otp.getEmail()));
        
        user.setSecurityPin(request.getNewSecurityPin());
        userRepository.save(user);
        
        otp.setIsUsed(true);
        otpRepository.save(otp);
    }
    
    public void verifyOTP(OTPVerificationRequest request) {
        OTP otp = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));
        
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }
        
        otp.setIsUsed(true);
        otpRepository.save(otp);
    }
    
    private void saveOTP(String email, String phoneNumber, String otpCode, OTP.OTPType otpType) {
        OTP otp = new OTP();
        otp.setEmail(email);
        otp.setPhoneNumber(phoneNumber);
        otp.setOtpCode(otpCode);
        otp.setOtpType(otpType);
        otp.setIsUsed(false);
        otp.setIsExpired(false);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        otpRepository.save(otp);
    }
}
