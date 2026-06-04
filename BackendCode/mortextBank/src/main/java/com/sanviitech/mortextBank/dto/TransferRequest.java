package com.sanviitech.mortextBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    
    private String fromAccountNumber;
    
    private String toAccountNumber;
    
    private BigDecimal amount;
    
    private String remarks;
    
    private String transferType;
    
    private String beneficiaryId;
}
