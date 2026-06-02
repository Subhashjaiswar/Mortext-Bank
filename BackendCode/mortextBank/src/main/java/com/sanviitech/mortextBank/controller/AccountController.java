package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.ApiResponse;
import com.sanviitech.mortextBank.entity.Account;
import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "Account Management APIs")
public class AccountController {
    
    private final AccountService accountService;
    
    @GetMapping
    @Operation(summary = "Get all user accounts")
    public ResponseEntity<ApiResponse<List<Account>>> getUserAccounts(Authentication authentication) {
        List<Account> accounts = accountService.getUserAccounts(authentication);
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
    }
    
    @GetMapping("/{accountId}")
    @Operation(summary = "Get account by ID")
    public ResponseEntity<ApiResponse<Account>> getAccountById(@PathVariable Long accountId, Authentication authentication) {
        Account account = accountService.getAccountById(accountId, authentication);
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account));
    }
    
    @GetMapping("/{accountId}/statement")
    @Operation(summary = "Get account statement")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAccountStatement(@PathVariable Long accountId, Authentication authentication) {
        List<Transaction> transactions = accountService.getAccountStatement(accountId, authentication);
        return ResponseEntity.ok(ApiResponse.success("Statement retrieved successfully", transactions));
    }
    
    @GetMapping("/{accountId}/statement/range")
    @Operation(summary = "Get account statement by date range")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAccountStatementByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        List<Transaction> transactions = accountService.getAccountStatementByDateRange(accountId, startDate, endDate, authentication);
        return ResponseEntity.ok(ApiResponse.success("Statement retrieved successfully", transactions));
    }
    
    @GetMapping("/{accountId}/statement/download")
    @Operation(summary = "Download account statement as PDF")
    public ResponseEntity<byte[]> downloadStatementPDF(@PathVariable Long accountId, Authentication authentication) throws Exception {
        byte[] pdfBytes = accountService.downloadStatementPDF(accountId, authentication);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "account_statement.pdf");
        headers.setContentLength(pdfBytes.length);
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/savings/balance")
    @Operation(summary = "Get savings account balance")
    public ResponseEntity<ApiResponse<Account>> getSavingsAccountBalance(Authentication authentication) {
        Account account = accountService.getSavingsAccountBalance(authentication);
        return ResponseEntity.ok(ApiResponse.success("Savings account balance retrieved successfully", account));
    }
    
    @GetMapping("/savings/balance/{userId}")
    @Operation(summary = "Get savings account balance by user ID")
    public ResponseEntity<ApiResponse<Account>> getSavingsAccountBalanceByUserId(@PathVariable Long userId) {
        Account account = accountService.getSavingsAccountBalanceByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Savings account balance retrieved successfully", account));
    }
}
