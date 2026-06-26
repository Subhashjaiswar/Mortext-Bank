package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.entity.KYC;
import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface AdminService {
    public Page<User> getAllUsers(Pageable pageable);
    public User getUserById(Long userId);
    public User toggleUserStatus(Long userId);
    public Page<Transaction> getAllTransactions(Pageable pageable);
    public Page<Transaction> getTransactionsByStatus(Transaction.TransactionStatus status, Pageable pageable);
    public Transaction markTransactionAsFraudulent(Long transactionId, String reason);
    public Page<KYC> getAllKYCRequests(Pageable pageable);
    public Page<KYC> getPendingKYCRequests(Pageable pageable);
    public KYC approveKYC(Long kycId);
    public KYC rejectKYC(Long kycId, String reason);
    public Map<String, Object> getAnalytics();
    public User assignRole(Long userId, String role);
}