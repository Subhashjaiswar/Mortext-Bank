package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.ApiResponse;
import com.sanviitech.mortextBank.dto.TransactionResponse;
import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction History", description = "Transaction History APIs")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @GetMapping
    @Operation(summary = "Get all user transactions with pagination")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getUserTransactions(Authentication authentication, Pageable pageable) {
        Page<TransactionResponse> transactions = transactionService.getUserTransactions(authentication, pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }
    
    @GetMapping("/range")
    @Operation(summary = "Get transactions by date range")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(startDate, endDate, authentication);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }
    
    @GetMapping("/type/{transactionType}")
    @Operation(summary = "Get transactions by type")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionsByType(
            @PathVariable Transaction.TransactionType transactionType,
            Authentication authentication,
            Pageable pageable) {
        Page<TransactionResponse> transactions = transactionService.getTransactionsByType(transactionType, authentication, pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get transactions by status")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionsByStatus(
            @PathVariable Transaction.TransactionStatus status,
            Authentication authentication,
            Pageable pageable) {
        Page<TransactionResponse> transactions = transactionService.getTransactionsByStatus(status, authentication, pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }
}
