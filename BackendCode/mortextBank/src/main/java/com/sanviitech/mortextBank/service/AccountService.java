package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.entity.Account;
import com.sanviitech.mortextBank.entity.Transaction;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountService {
    List<Account> getUserAccounts(Authentication authentication);
    Account getAccountById(Long accountId, Authentication authentication);
    List<Transaction> getAccountStatement(Long accountId, Authentication authentication);
    List<Transaction> getAccountStatementByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate, Authentication authentication);
    byte[] downloadStatementPDF(Long accountId, Authentication authentication) throws Exception;
    Account getSavingsAccountBalance(Authentication authentication);
    Account getSavingsAccountBalanceByUserId(Long userId);
}