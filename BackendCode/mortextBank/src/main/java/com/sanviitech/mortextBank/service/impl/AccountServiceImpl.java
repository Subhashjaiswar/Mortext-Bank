package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.dto.AccountResponse;
import com.sanviitech.mortextBank.dto.TransactionResponse;
import com.sanviitech.mortextBank.entity.Account;
import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.constants.ValidationConstants;
import com.sanviitech.mortextBank.util.GlobalException;
import com.sanviitech.mortextBank.mapper.AccountMapper;
import com.sanviitech.mortextBank.mapper.TransactionMapper;
import com.sanviitech.mortextBank.repository.AccountRepository;
import com.sanviitech.mortextBank.repository.TransactionRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import com.sanviitech.mortextBank.service.AccountService;
import com.sanviitech.mortextBank.util.PDFGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PDFGenerator pdfGenerator;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    @Override
    public List<AccountResponse> getUserAccounts(Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching user accounts for email: {}", email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching accounts - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });
            List<Account> accounts = accountRepository.findByUserId(user.getId());
            List<AccountResponse> responses = accounts.stream()
                    .map(accountMapper::toResponse)
                    .collect(Collectors.toList());
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched {} accounts for user: {} in {} ms", responses.size(), email, (endTime - startTime));
            return responses;
        } catch (Exception e) {
            log.error("Failed to fetch accounts for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AccountResponse getAccountById(Long accountId, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching account by ID: {} for user: {}", accountId, email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching account - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });

            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> {
                        log.warn("Account not found with ID: {} for user: {}", accountId, email);
                        return GlobalException.resourceNotFound("Account", "id", accountId);
                    });

            if (!account.getUser().getId().equals(user.getId())) {
                log.warn("Unauthorized access attempt to account: {} by user: {}", accountId, email);
                throw new RuntimeException(ValidationConstants.UNAUTHORIZED_ACCESS_TO_ACCOUNT);
            }

            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched account: {} for user: {} in {} ms", accountId, email, (endTime - startTime));
            return accountMapper.toResponse(account);
        } catch (Exception e) {
            log.error("Failed to fetch account: {} for user: {} - Error: {}", accountId, email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransactionResponse> getAccountStatement(Long accountId, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        log.info("Fetching account statement for account ID: {}", accountId);
        
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> {
                        log.warn("Account not found for statement - ID: {}", accountId);
                        return GlobalException.resourceNotFound("Account", "id", accountId);
                    });
            List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
            List<TransactionResponse> responses = transactions.stream()
                    .map(transactionMapper::toResponse)
                    .collect(Collectors.toList());
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched {} transactions for account: {} in {} ms", responses.size(), accountId, (endTime - startTime));
            return responses;
        } catch (Exception e) {
            log.error("Failed to fetch statement for account: {} - Error: {}", accountId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TransactionResponse> getAccountStatementByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        log.info("Fetching account statement for account ID: {} between {} and {}", accountId, startDate, endDate);
        
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> {
                        log.warn("Account not found for date range statement - ID: {}", accountId);
                        return GlobalException.resourceNotFound("Account", "id", accountId);
                    });
            List<Transaction> transactions = transactionRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate);
            List<TransactionResponse> responses = transactions.stream()
                    .map(transactionMapper::toResponse)
                    .collect(Collectors.toList());
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched {} transactions for account: {} in date range in {} ms", responses.size(), accountId, (endTime - startTime));
            return responses;
        } catch (Exception e) {
            log.error("Failed to fetch date range statement for account: {} - Error: {}", accountId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public byte[] downloadStatementPDF(Long accountId, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        log.info("Generating PDF statement for account ID: {}", accountId);
        
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> {
                        log.warn("Account not found for PDF generation - ID: {}", accountId);
                        return GlobalException.resourceNotFound("Account", "id", accountId);
                    });
            List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
            byte[] pdf = pdfGenerator.generateStatement(transactions, account.getAccountNumber(), account.getUser().getFullName());
            
            long endTime = System.currentTimeMillis();
            log.info("PDF statement generated successfully for account: {} in {} ms", accountId, (endTime - startTime));
            return pdf;
        } catch (Exception e) {
            log.error("Failed to generate PDF for account: {} - Error: {}", accountId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AccountResponse getSavingsAccountBalance(Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching savings account balance for user: {}", email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching savings balance - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });

            Account account = accountRepository.findByUserIdAndAccountType(user.getId(), Account.AccountType.SAVINGS)
                    .orElseThrow(() -> {
                        log.warn("Savings account not found for user: {}", user.getId());
                        return GlobalException.resourceNotFound("Savings Account", "user_id", user.getId());
                    });
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched savings balance for user: {} in {} ms", email, (endTime - startTime));
            return accountMapper.toResponse(account);
        } catch (Exception e) {
            log.error("Failed to fetch savings balance for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AccountResponse getSavingsAccountBalanceByUserId(Long userId) {
        long startTime = System.currentTimeMillis();
        log.info("Fetching savings account balance for user ID: {}", userId);
        
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching savings balance by ID - ID: {}", userId);
                        return GlobalException.resourceNotFound("User", "id", userId);
                    });

            Account account = accountRepository.findByUserIdAndAccountType(userId, Account.AccountType.SAVINGS)
                    .orElseThrow(() -> {
                        log.warn("Savings account not found for user ID: {}", userId);
                        return GlobalException.resourceNotFound("Savings Account", "user_id", userId);
                    });
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched savings balance for user ID: {} in {} ms", userId, (endTime - startTime));
            return accountMapper.toResponse(account);
        } catch (Exception e) {
            log.error("Failed to fetch savings balance for user ID: {} - Error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
}