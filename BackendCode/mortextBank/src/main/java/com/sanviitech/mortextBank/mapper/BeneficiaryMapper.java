package com.sanviitech.mortextBank.mapper;

import com.sanviitech.mortextBank.dto.BeneficiaryResponse;
import com.sanviitech.mortextBank.entity.Beneficiary;
import org.springframework.stereotype.Component;

@Component
public class BeneficiaryMapper {
    
    public BeneficiaryResponse toResponse(Beneficiary beneficiary) {
        if (beneficiary == null) {
            return null;
        }
        
        BeneficiaryResponse response = new BeneficiaryResponse();
        response.setId(beneficiary.getId());
        response.setBeneficiaryName(beneficiary.getBeneficiaryName());
        response.setAccountNumber(beneficiary.getAccountNumber());
        response.setIfscCode(beneficiary.getIfscCode());
        response.setBankName(beneficiary.getBankName());
        response.setBranch(beneficiary.getBranch());
        response.setUpiId(beneficiary.getUpiId());
        response.setBeneficiaryType(beneficiary.getBeneficiaryType());
        response.setNickname(beneficiary.getNickname());
        response.setIsActive(beneficiary.getIsActive());
        response.setCreatedAt(beneficiary.getCreatedAt());
        response.setUpdatedAt(beneficiary.getUpdatedAt());
        return response;
    }
}
