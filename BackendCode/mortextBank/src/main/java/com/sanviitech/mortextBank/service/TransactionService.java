package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Page<Transaction> getUserTransactions(Authentication authentication, Pageable pageable);
    List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Authentication authentication);
    Page<Transaction> getTransactionsByType(Transaction.TransactionType transactionType, Authentication authentication, Pageable pageable);
    Page<Transaction> getTransactionsByStatus(Transaction.TransactionStatus status, Authentication authentication, Pageable pageable);
}