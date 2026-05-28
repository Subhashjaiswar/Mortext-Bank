package com.sanviitech.mortextBank.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBeneficiaryRequest {
    //jsonIgnone for hiden field if you want to hide the field from the response
    @NotBlank(message = "Beneficiary name is required")
    @Size(max = 100, message = "Beneficiary name must not exceed 100 characters")
    private String beneficiaryName;
    
    @NotBlank(message = "Account number is required")
    @Size(min = 9, max = 20, message = "Account number must be between 9 and 20 characters")
    private String accountNumber;
    
    @Size(max = 50, message = "IFSC code must not exceed 50 characters")
    private String ifscCode;
    
    @Size(max = 50, message = "Bank name must not exceed 50 characters")
    private String bankName;
    
    @Size(max = 50, message = "Branch must not exceed 50 characters")
    private String branch;
    
    @Size(max = 50, message = "UPI ID must not exceed 50 characters")
    private String upiId;
    
    @NotBlank(message = "Beneficiary type is required")
    private String beneficiaryType;
    
    @Size(max = 500, message = "Nickname must not exceed 500 characters")
    private String nickname;
}
