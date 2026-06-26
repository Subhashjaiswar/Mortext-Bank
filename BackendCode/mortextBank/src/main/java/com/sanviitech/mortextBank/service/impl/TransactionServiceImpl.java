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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Page<TransactionResponse> getUserTransactions(Authentication authentication, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching user transactions for email: {} with pagination", email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching transactions - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });
            Page<Transaction> transactions = transactionRepository.findByUserId(user.getId(), pageable);
            Page<TransactionResponse> responses = transactions.map(transactionMapper::toResponse);
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched {} transactions for user: {} in {} ms", responses.getTotalElements(), email, (endTime - startTime));
            return responses;
        } catch (Exception e) {
            log.error("Failed to fetch transactions for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching transactions for email: {} between {} and {}", email, startDate, endDate);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching transactions by date range - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });
            List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(user.getId(), startDate, endDate);
            List<TransactionResponse> responses = transactions.stream()
                    .map(transactionMapper::toResponse)
                    .collect(Collectors.toList());
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched {} transactions for user: {} in date range in {} ms", responses.size(), email, (endTime - startTime));
            return responses;
        } catch (Exception e) {
            log.error("Failed to fetch transactions by date range for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Page<TransactionResponse> getTransactionsByType(Transaction.TransactionType transactionType, Authentication authentication, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching transactions of type: {} for email: {}", transactionType, email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching transactions by type - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });
            Page<Transaction> transactions = transactionRepository.findByUserIdAndTransactionType(user.getId(), transactionType, pageable);
            Page<TransactionResponse> responses = transactions.map(transactionMapper::toResponse);
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched {} transactions of type: {} for user: {} in {} ms", responses.getTotalElements(), transactionType, email, (endTime - startTime));
            return responses;
        } catch (Exception e) {
            log.error("Failed to fetch transactions by type for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Page<TransactionResponse> getTransactionsByStatus(Transaction.TransactionStatus status, Authentication authentication, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching transactions with status: {} for email: {}", status, email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching transactions by status - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });
            Page<Transaction> transactions = transactionRepository.findByUserIdAndStatus(user.getId(), status, pageable);
            Page<TransactionResponse> responses = transactions.map(transactionMapper::toResponse);
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched {} transactions with status: {} for user: {} in {} ms", responses.getTotalElements(), status, email, (endTime - startTime));
            return responses;
        } catch (Exception e) {
            log.error("Failed to fetch transactions by status for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }
}