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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        return accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse getAccountById(Long accountId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> GlobalException.resourceNotFound("Account", "id", accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(ValidationConstants.UNAUTHORIZED_ACCESS_TO_ACCOUNT);
        }

        return accountMapper.toResponse(account);
    }

    @Override
    public List<TransactionResponse> getAccountStatement(Long accountId, Authentication authentication) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> GlobalException.resourceNotFound("Account", "id", accountId));
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getAccountStatementByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate, Authentication authentication) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> GlobalException.resourceNotFound("Account", "id", accountId));
        List<Transaction> transactions = transactionRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] downloadStatementPDF(Long accountId, Authentication authentication) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> GlobalException.resourceNotFound("Account", "id", accountId));
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return pdfGenerator.generateStatement(transactions, account.getAccountNumber(),
                account.getUser().getFullName());
    }

    @Override
    public AccountResponse getSavingsAccountBalance(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

        Account account = accountRepository.findByUserIdAndAccountType(user.getId(), Account.AccountType.SAVINGS)
                .orElseThrow(() -> GlobalException.resourceNotFound("Savings Account", "user_id", user.getId()));
        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse getSavingsAccountBalanceByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "id", userId));

        Account account = accountRepository.findByUserIdAndAccountType(userId, Account.AccountType.SAVINGS)
                .orElseThrow(() -> GlobalException.resourceNotFound("Savings Account", "user_id", userId));
        return accountMapper.toResponse(account);
    }
}