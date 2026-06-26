package com.sanviitech.mortextBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYCUploadRequest {
    
    private String panNumber;
    
    private String aadhaarNumber;
    
    private String idProofType;
    
    private String idProofNumber;
    
    private String idProofImageUrl;
    
    private String addressProofImageUrl;
    
    private String selfieImageUrl;
    
    private String panCardImageUrl;
    
    private String aadhaarCardImageUrl;
    
    private String dateOfBirth;
    
    private String permanentAddress;
    
    private String currentAddress;
    
    private String city;
    
    private String state;
    
    private String pincode;
    
    private String nationality;
}
