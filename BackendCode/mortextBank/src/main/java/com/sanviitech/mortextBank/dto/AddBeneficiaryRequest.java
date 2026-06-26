package com.sanviitech.mortextBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBeneficiaryRequest {

    private String beneficiaryName;
    
    private String accountNumber;
    
    private String ifscCode;
    
    private String bankName;
    
    private String branch;
    
    private String upiId;
    
    private String beneficiaryType;
    
    private String nickname;
}
