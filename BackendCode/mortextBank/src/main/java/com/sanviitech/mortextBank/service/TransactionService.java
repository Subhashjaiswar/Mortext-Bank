package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.exception.ResourceNotFoundException;
import com.sanviitech.mortextBank.repository.TransactionRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    
    public Page<Transaction> getUserTransactions(Authentication authentication, Pageable pageable) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return transactionRepository.findByUserId(user.getId(), pageable);
    }
    
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return transactionRepository.findByUserIdAndTransactionDateBetween(user.getId(), startDate, endDate);
    }
    
    public Page<Transaction> getTransactionsByType(Transaction.TransactionType transactionType, Authentication authentication, Pageable pageable) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return transactionRepository.findByUserIdAndTransactionType(user.getId(), transactionType, pageable);
    }
    
    public Page<Transaction> getTransactionsByStatus(Transaction.TransactionStatus status, Authentication authentication, Pageable pageable) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return transactionRepository.findByUserIdAndStatus(user.getId(), status, pageable);
    }
}
