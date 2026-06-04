package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.AccountResponse;
import com.sanviitech.mortextBank.dto.TransactionResponse;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountService {
    List<AccountResponse> getUserAccounts(Authentication authentication);
    AccountResponse getAccountById(Long accountId, Authentication authentication);
    List<TransactionResponse> getAccountStatement(Long accountId, Authentication authentication);
    List<TransactionResponse> getAccountStatementByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate, Authentication authentication);
    byte[] downloadStatementPDF(Long accountId, Authentication authentication);
    AccountResponse getSavingsAccountBalance(Authentication authentication);
    AccountResponse getSavingsAccountBalanceByUserId(Long userId);
}