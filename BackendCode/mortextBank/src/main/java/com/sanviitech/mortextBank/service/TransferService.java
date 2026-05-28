package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.AddBeneficiaryRequest;
import com.sanviitech.mortextBank.dto.ScheduledTransferRequest;
import com.sanviitech.mortextBank.dto.TransferRequest;
import com.sanviitech.mortextBank.entity.*;
import com.sanviitech.mortextBank.exception.InsufficientBalanceException;
import com.sanviitech.mortextBank.exception.ResourceNotFoundException;
import com.sanviitech.mortextBank.repository.*;
import com.sanviitech.mortextBank.util.OTPUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {
    
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ScheduledTransferRepository scheduledTransferRepository;
    
    @Transactional
    public Transaction transferMoney(TransferRequest request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getFromAccountNumber()));
        
        if (!fromAccount.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }
        
        if (fromAccount.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account");
        }
        
        fromAccount.setAvailableBalance(fromAccount.getAvailableBalance().subtract(request.getAmount()));
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(fromAccount);
        
        Transaction transaction = new Transaction();
        transaction.setTransactionId(OTPUtil.generateTransactionId());
        transaction.setAccount(fromAccount);
        transaction.setUser(user);
        transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(fromAccount.getBalance());
        transaction.setDescription(request.getRemarks());
        transaction.setBeneficiaryAccountNumber(request.getToAccountNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        
        return transactionRepository.save(transaction);
    }
    
    public Beneficiary addBeneficiary(AddBeneficiaryRequest request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(user);
        beneficiary.setBeneficiaryName(request.getBeneficiaryName());
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setIfscCode(request.getIfscCode());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setBranch(request.getBranch());
        beneficiary.setUpiId(request.getUpiId());
        beneficiary.setBeneficiaryType(Beneficiary.BeneficiaryType.valueOf(request.getBeneficiaryType()));
        beneficiary.setNickname(request.getNickname());
        beneficiary.setIsActive(true);
        
        return beneficiaryRepository.save(beneficiary);
    }
    
    public java.util.List<Beneficiary> getBeneficiaries(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return beneficiaryRepository.findByUserId(user.getId());
    }
    
    public ScheduledTransfer scheduleTransfer(ScheduledTransferRequest request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getFromAccountNumber()));
        
        if (!fromAccount.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }
        
        ScheduledTransfer scheduledTransfer = new ScheduledTransfer();
        scheduledTransfer.setFromAccount(fromAccount);
        scheduledTransfer.setUser(user);
        scheduledTransfer.setToAccountNumber(request.getToAccountNumber());
        scheduledTransfer.setToAccountName(request.getToAccountName());
        scheduledTransfer.setAmount(request.getAmount());
        scheduledTransfer.setRemarks(request.getRemarks());
        scheduledTransfer.setTransferType(ScheduledTransfer.TransferType.valueOf(request.getTransferType()));
        scheduledTransfer.setScheduleType(ScheduledTransfer.ScheduleType.valueOf(request.getScheduleType()));
        scheduledTransfer.setScheduledDate(request.getScheduledDate());
        scheduledTransfer.setIsRecurring(request.getIsRecurring());
        scheduledTransfer.setRecurringIntervalDays(request.getRecurringIntervalDays());
        scheduledTransfer.setEndDate(request.getEndDate());
        scheduledTransfer.setStatus(ScheduledTransfer.TransferStatus.SCHEDULED);
        
        return scheduledTransferRepository.save(scheduledTransfer);
    }
}
