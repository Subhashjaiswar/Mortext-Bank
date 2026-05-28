package com.sanviitech.mortextBank.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYCUploadRequest {
    
    @Size(max = 100, message = "PAN number must not exceed 100 characters")
    private String panNumber;
    
    @Size(max = 100, message = "Aadhaar number must not exceed 100 characters")
    private String aadhaarNumber;
    
    @NotBlank(message = "ID proof type is required")
    private String idProofType;
    
    @NotBlank(message = "ID proof number is required")
    private String idProofNumber;
    
    @Size(max = 500, message = "ID proof image URL must not exceed 500 characters")
    private String idProofImageUrl;
    
    @Size(max = 500, message = "Address proof image URL must not exceed 500 characters")
    private String addressProofImageUrl;
    
    @Size(max = 500, message = "Selfie image URL must not exceed 500 characters")
    private String selfieImageUrl;
    
    @Size(max = 500, message = "PAN card image URL must not exceed 500 characters")
    private String panCardImageUrl;
    
    @Size(max = 500, message = "Aadhaar card image URL must not exceed 500 characters")
    private String aadhaarCardImageUrl;
    
    @Size(max = 100, message = "Date of birth must not exceed 100 characters")
    private String dateOfBirth;
    
    @Size(max = 500, message = "Permanent address must not exceed 500 characters")
    private String permanentAddress;
    
    @Size(max = 500, message = "Current address must not exceed 500 characters")
    private String currentAddress;
    
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;
    
    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;
    
    @Size(max = 10, message = "Pincode must not exceed 10 characters")
    private String pincode;
    
    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;
}
