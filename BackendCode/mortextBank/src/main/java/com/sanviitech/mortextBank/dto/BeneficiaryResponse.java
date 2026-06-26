package com.sanviitech.mortextBank.dto;

import com.sanviitech.mortextBank.entity.Beneficiary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryResponse {
    
    private Long id;
    private String beneficiaryName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String branch;
    private String upiId;
    private Beneficiary.BeneficiaryType beneficiaryType;
    private String nickname;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
