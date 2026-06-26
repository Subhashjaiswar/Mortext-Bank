package com.sanviitech.mortextBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    
    private String transactionId;
    private Long userId;
    private String userEmail;
    private String userName;
    private Long accountId;
    private String accountNumber;
    private String transactionType;
    private String status;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String beneficiaryAccountNumber;
    private String beneficiaryName;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
}
