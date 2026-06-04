package com.sanviitech.mortextBank.dto;

import com.sanviitech.mortextBank.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    
    private Long id;
    private String accountNumber;
    private Account.AccountType accountType;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private String accountName;
    private String currency;
    private Boolean isActive;
    private Boolean isFrozen;
    private LocalDateTime createdAt;
    private LocalDateTime accountOpenedAt;
    private LocalDateTime updatedAt;
}
