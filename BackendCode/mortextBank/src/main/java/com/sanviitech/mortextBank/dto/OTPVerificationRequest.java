package com.sanviitech.mortextBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTPVerificationRequest {
    
    private String email;
    
    private String otp;
    
    private String otpType;
}
