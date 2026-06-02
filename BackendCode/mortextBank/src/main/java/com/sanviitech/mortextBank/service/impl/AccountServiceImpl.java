package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.entity.Account;
import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.exception.ResourceNotFoundException;
import com.sanviitech.mortextBank.repository.AccountRepository;
import com.sanviitech.mortextBank.repository.TransactionRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import com.sanviitech.mortextBank.service.AccountService;
import com.sanviitech.mortextBank.util.PDFGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PDFGenerator pdfGenerator;

    @Override
    public List<Account> getUserAccounts(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return accountRepository.findByUserId(user.getId());
    }

    @Override
    public Account getAccountById(Long accountId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }

        return account;
    }

    @Override
    public List<Transaction> getAccountStatement(Long accountId, Authentication authentication) {
        Account account = getAccountById(accountId, authentication);
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public List<Transaction> getAccountStatementByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate, Authentication authentication) {
        Account account = getAccountById(accountId, authentication);
        return transactionRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate);
    }

    @Override
    public byte[] downloadStatementPDF(Long accountId, Authentication authentication) throws Exception {
        Account account = getAccountById(accountId, authentication);
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return pdfGenerator.generateStatement(transactions, account.getAccountNumber(),
                account.getUser().getFullName());
    }

    @Override
    public Account getSavingsAccountBalance(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));

        return accountRepository.findByUserIdAndAccountType(user.getId(), Account.AccountType.SAVINGS)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Account", "user_id", user.getId()));
    }

    @Override
    public Account getSavingsAccountBalanceByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return accountRepository.findByUserIdAndAccountType(userId, Account.AccountType.SAVINGS)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Account", "user_id", userId));
    }
}