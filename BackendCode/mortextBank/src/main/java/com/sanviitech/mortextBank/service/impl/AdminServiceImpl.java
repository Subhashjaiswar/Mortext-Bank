package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.entity.*;
import com.sanviitech.mortextBank.exception.BadRequestException;
import com.sanviitech.mortextBank.exception.ResourceNotFoundException;
import com.sanviitech.mortextBank.repository.*;
import com.sanviitech.mortextBank.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final KYCRepository kycRepository;

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    public User toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        // User status toggle removed - User entity no longer has enabled field
        return userRepository.save(user);
    }

    @Override
    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    public Page<Transaction> getTransactionsByStatus(Transaction.TransactionStatus status, Pageable pageable) {
        return transactionRepository.findByUserIdAndStatus(null, status, pageable);
    }

    @Override
    public Transaction markTransactionAsFraudulent(Long transactionId, String reason) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        transaction.setIsFraudulent(true);
        transaction.setFraudReason(reason);
        return transactionRepository.save(transaction);
    }

    @Override
    public Page<KYC> getAllKYCRequests(Pageable pageable) {
        return kycRepository.findAll(pageable);
    }

    @Override
    public Page<KYC> getPendingKYCRequests(Pageable pageable) {
        return kycRepository.findByStatus(KYC.KYCStatus.PENDING, pageable);
    }

    @Override
    public KYC approveKYC(Long kycId) {
        KYC kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC", "id", kycId));

        kyc.setStatus(KYC.KYCStatus.APPROVED);
        kyc.setVerifiedAt(LocalDateTime.now());
        return kycRepository.save(kyc);
    }

    @Override
    public KYC rejectKYC(Long kycId, String reason) {
        KYC kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC", "id", kycId));

        kyc.setStatus(KYC.KYCStatus.REJECTED);
        kyc.setRejectionReason(reason);
        return kycRepository.save(kyc);
    }

    @Override
    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        long totalUsers = userRepository.count();
        long activeUsers = totalUsers; // All users are now considered active
        long totalAccounts = accountRepository.count();
        long totalTransactions = transactionRepository.count();

        BigDecimal totalBalance = accountRepository.findAll().stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingKYC = kycRepository.findAll().stream()
                .filter(kyc -> kyc.getStatus() == KYC.KYCStatus.PENDING)
                .count();

        long fraudulentTransactions = transactionRepository.findAll().stream()
                .filter(Transaction::getIsFraudulent)
                .count();

        analytics.put("totalUsers", totalUsers);
        analytics.put("activeUsers", activeUsers);
        analytics.put("totalAccounts", totalAccounts);
        analytics.put("totalTransactions", totalTransactions);
        analytics.put("totalBalance", totalBalance);
        analytics.put("pendingKYC", pendingKYC);
        analytics.put("fraudulentTransactions", fraudulentTransactions);

        return analytics;
    }
}