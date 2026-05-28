package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.entity.Account;
import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.repository.AccountRepository;
import com.sanviitech.mortextBank.repository.TransactionRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    public Map<String, Object> getDashboardData(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<Transaction> recentTransactions = transactionRepository.findByUserId(user.getId(), 
                org.springframework.data.domain.PageRequest.of(0, 10)).getContent();
        
        BigDecimal totalCredit = recentTransactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.CREDIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDebit = recentTransactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.DEBIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("welcomeMessage", "Welcome back, " + user.getFullName() + "!");
        dashboardData.put("totalBalance", totalBalance);
        dashboardData.put("availableBalance", totalBalance);
        dashboardData.put("recentTransactions", recentTransactions);
        dashboardData.put("totalCredit", totalCredit);
        dashboardData.put("totalDebit", totalDebit);
        dashboardData.put("accountCount", accounts.size());
        
        return dashboardData;
    }
}
