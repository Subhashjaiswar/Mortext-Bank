package com.sanviitech.mortextBank.dto;

import com.sanviitech.mortextBank.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    
    private Long id;
    private String transactionId;
    private Transaction.TransactionType transactionType;
    private Transaction.TransactionStatus status;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceNumber;
    private String beneficiaryAccountNumber;
    private String beneficiaryName;
    private String category;
    private String remarks;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isFraudulent;
    private String fraudReason;
}
