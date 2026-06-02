package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.exception.ResourceNotFoundException;
import com.sanviitech.mortextBank.repository.TransactionRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import com.sanviitech.mortextBank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Page<Transaction> getUserTransactions(Authentication authentication, Pageable pageable) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return transactionRepository.findByUserId(user.getId(), pageable);
    }

    @Override
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return transactionRepository.findByUserIdAndTransactionDateBetween(user.getId(), startDate, endDate);
    }

    @Override
    public Page<Transaction> getTransactionsByType(Transaction.TransactionType transactionType, Authentication authentication, Pageable pageable) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return transactionRepository.findByUserIdAndTransactionType(user.getId(), transactionType, pageable);
    }

    @Override
    public Page<Transaction> getTransactionsByStatus(Transaction.TransactionStatus status, Authentication authentication, Pageable pageable) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return transactionRepository.findByUserIdAndStatus(user.getId(), status, pageable);
    }
}