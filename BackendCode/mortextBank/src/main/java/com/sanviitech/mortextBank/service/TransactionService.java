package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.TransactionResponse;
import com.sanviitech.mortextBank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Page<TransactionResponse> getUserTransactions(Authentication authentication, Pageable pageable);
    List<TransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Authentication authentication);
    Page<TransactionResponse> getTransactionsByType(Transaction.TransactionType transactionType, Authentication authentication, Pageable pageable);
    Page<TransactionResponse> getTransactionsByStatus(Transaction.TransactionStatus status, Authentication authentication, Pageable pageable);
}