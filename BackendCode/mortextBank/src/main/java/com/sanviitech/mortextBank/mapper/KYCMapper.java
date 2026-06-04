package com.sanviitech.mortextBank.mapper;

import com.sanviitech.mortextBank.dto.KYCResponse;
import com.sanviitech.mortextBank.entity.KYC;
import org.springframework.stereotype.Component;

@Component
public class KYCMapper {
    
    public KYCResponse toResponse(KYC kyc) {
        if (kyc == null) {
            return null;
        }
        
        KYCResponse response = new KYCResponse();
        response.setId(kyc.getId());
        response.setPanNumber(kyc.getPanNumber());
        response.setAadhaarNumber(kyc.getAadhaarNumber());
        response.setIdProofType(kyc.getIdProofType());
        response.setIdProofNumber(kyc.getIdProofNumber());
        response.setIdProofImageUrl(kyc.getIdProofImageUrl());
        response.setAddressProofImageUrl(kyc.getAddressProofImageUrl());
        response.setSelfieImageUrl(kyc.getSelfieImageUrl());
        response.setPanCardImageUrl(kyc.getPanCardImageUrl());
        response.setAadhaarCardImageUrl(kyc.getAadhaarCardImageUrl());
        response.setDateOfBirth(kyc.getDateOfBirth());
        response.setPermanentAddress(kyc.getPermanentAddress());
        response.setCurrentAddress(kyc.getCurrentAddress());
        response.setCity(kyc.getCity());
        response.setState(kyc.getState());
        response.setPincode(kyc.getPincode());
        response.setNationality(kyc.getNationality());
        response.setStatus(kyc.getStatus());
        response.setRejectionReason(kyc.getRejectionReason());
        response.setSubmittedAt(kyc.getSubmittedAt());
        response.setVerifiedAt(kyc.getVerifiedAt());
        response.setUpdatedAt(kyc.getUpdatedAt());
        return response;
    }
}
