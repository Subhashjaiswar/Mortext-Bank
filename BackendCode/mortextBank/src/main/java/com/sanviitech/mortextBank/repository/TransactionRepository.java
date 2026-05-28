package com.sanviitech.mortextBank.repository;

import com.sanviitech.mortextBank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionId(String transactionId);
    
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
    
    List<Transaction> findByAccountId(Long accountId);
    
    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findByAccountIdAndTransactionDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.transactionType = :transactionType")
    Page<Transaction> findByUserIdAndTransactionType(@Param("userId") Long userId, 
                                                     @Param("transactionType") Transaction.TransactionType transactionType, 
                                                     Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.status = :status")
    Page<Transaction> findByUserIdAndStatus(@Param("userId") Long userId, 
                                           @Param("status") Transaction.TransactionStatus status, 
                                           Pageable pageable);
}
