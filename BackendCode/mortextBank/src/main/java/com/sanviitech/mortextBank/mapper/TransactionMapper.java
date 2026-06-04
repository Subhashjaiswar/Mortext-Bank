package com.sanviitech.mortextBank.mapper;

import com.sanviitech.mortextBank.dto.TransactionResponse;
import com.sanviitech.mortextBank.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    
    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTransactionId(transaction.getTransactionId());
        response.setTransactionType(transaction.getTransactionType());
        response.setStatus(transaction.getStatus());
        response.setAmount(transaction.getAmount());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setDescription(transaction.getDescription());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setBeneficiaryAccountNumber(transaction.getBeneficiaryAccountNumber());
        response.setBeneficiaryName(transaction.getBeneficiaryName());
        response.setCategory(transaction.getCategory());
        response.setRemarks(transaction.getRemarks());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        response.setIsFraudulent(transaction.getIsFraudulent());
        response.setFraudReason(transaction.getFraudReason());
        return response;
    }
}
