package com.sanviitech.mortextBank.dto;

import com.sanviitech.mortextBank.entity.KYC;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYCResponse {
    
    private Long id;
    private String panNumber;
    private String aadhaarNumber;
    private KYC.IdProofType idProofType;
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
    private KYC.KYCStatus status;
    private String rejectionReason;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime updatedAt;
}
