package com.sanviitech.mortextBank.mapper;

import com.sanviitech.mortextBank.dto.AccountResponse;
import com.sanviitech.mortextBank.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    
    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }
        
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setAvailableBalance(account.getAvailableBalance());
        response.setAccountName(account.getAccountName());
        response.setCurrency(account.getCurrency());
        response.setIsActive(account.getIsActive());
        response.setIsFrozen(account.getIsFrozen());
        response.setCreatedAt(account.getCreatedAt());
        response.setAccountOpenedAt(account.getAccountOpenedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        return response;
    }
}
