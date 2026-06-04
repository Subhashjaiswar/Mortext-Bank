package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.dto.TransactionResponse;
import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.util.GlobalException;
import com.sanviitech.mortextBank.mapper.TransactionMapper;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Page<TransactionResponse> getUserTransactions(Authentication authentication, Pageable pageable) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));
        Page<Transaction> transactions = transactionRepository.findByUserId(user.getId(), pageable);
        return transactions.map(transactionMapper::toResponse);
    }

    @Override
    public List<TransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(user.getId(), startDate, endDate);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TransactionResponse> getTransactionsByType(Transaction.TransactionType transactionType, Authentication authentication, Pageable pageable) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));
        Page<Transaction> transactions = transactionRepository.findByUserIdAndTransactionType(user.getId(), transactionType, pageable);
        return transactions.map(transactionMapper::toResponse);
    }

    @Override
    public Page<TransactionResponse> getTransactionsByStatus(Transaction.TransactionStatus status, Authentication authentication, Pageable pageable) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));
        Page<Transaction> transactions = transactionRepository.findByUserIdAndStatus(user.getId(), status, pageable);
        return transactions.map(transactionMapper::toResponse);
    }
}