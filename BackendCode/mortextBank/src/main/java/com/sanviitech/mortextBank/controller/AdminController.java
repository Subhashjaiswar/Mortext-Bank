package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.ApiResponse;
import com.sanviitech.mortextBank.entity.KYC;
import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Panel", description = "Admin Panel APIs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(Pageable pageable) {
        Page<User> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long userId) {
        User user = adminService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    @PostMapping("/users/{userId}/toggle-status")
    @Operation(summary = "Toggle user status")
    public ResponseEntity<ApiResponse<User>> toggleUserStatus(@PathVariable Long userId) {
        User user = adminService.toggleUserStatus(userId);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", user));
    }
    
    @GetMapping("/transactions")
    @Operation(summary = "Get all transactions")
    public ResponseEntity<ApiResponse<Page<Transaction>>> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactions = adminService.getAllTransactions(pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }
    
    @GetMapping("/transactions/status/{status}")
    @Operation(summary = "Get transactions by status")
    public ResponseEntity<ApiResponse<Page<Transaction>>> getTransactionsByStatus(
            @PathVariable Transaction.TransactionStatus status,
            Pageable pageable) {
        Page<Transaction> transactions = adminService.getTransactionsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }
    
    @PostMapping("/transactions/{transactionId}/mark-fraudulent")
    @Operation(summary = "Mark transaction as fraudulent")
    public ResponseEntity<ApiResponse<Transaction>> markTransactionAsFraudulent(
            @PathVariable Long transactionId,
            @RequestParam String reason) {
        Transaction transaction = adminService.markTransactionAsFraudulent(transactionId, reason);
        return ResponseEntity.ok(ApiResponse.success("Transaction marked as fraudulent", transaction));
    }
    
    @GetMapping("/kyc")
    @Operation(summary = "Get all KYC requests")
    public ResponseEntity<ApiResponse<Page<KYC>>> getAllKYCRequests(Pageable pageable) {
        Page<KYC> kycRequests = adminService.getAllKYCRequests(pageable);
        return ResponseEntity.ok(ApiResponse.success("KYC requests retrieved successfully", kycRequests));
    }
    
    @GetMapping("/kyc/pending")
    @Operation(summary = "Get pending KYC requests")
    public ResponseEntity<ApiResponse<Page<KYC>>> getPendingKYCRequests(Pageable pageable) {
        Page<KYC> kycRequests = adminService.getPendingKYCRequests(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pending KYC requests retrieved successfully", kycRequests));
    }
    
    @PostMapping("/kyc/{kycId}/approve")
    @Operation(summary = "Approve KYC request")
    public ResponseEntity<ApiResponse<KYC>> approveKYC(@PathVariable Long kycId) {
        KYC kyc = adminService.approveKYC(kycId);
        return ResponseEntity.ok(ApiResponse.success("KYC approved successfully", kyc));
    }
    
    @PostMapping("/kyc/{kycId}/reject")
    @Operation(summary = "Reject KYC request")
    public ResponseEntity<ApiResponse<KYC>> rejectKYC(
            @PathVariable Long kycId,
            @RequestParam String reason) {
        KYC kyc = adminService.rejectKYC(kycId, reason);
        return ResponseEntity.ok(ApiResponse.success("KYC rejected successfully", kyc));
    }
    
    @GetMapping("/analytics")
    @Operation(summary = "Get analytics dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics() {
        Map<String, Object> analytics = adminService.getAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
    }
}
