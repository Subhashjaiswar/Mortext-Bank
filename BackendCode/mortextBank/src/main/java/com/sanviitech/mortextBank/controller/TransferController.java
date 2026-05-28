package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.*;
import com.sanviitech.mortextBank.entity.Beneficiary;
import com.sanviitech.mortextBank.entity.ScheduledTransfer;
import com.sanviitech.mortextBank.entity.Transaction;
import com.sanviitech.mortextBank.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Money Transfer", description = "Money Transfer APIs")
public class TransferController {
    
    private final TransferService transferService;
    
    @PostMapping
    @Operation(summary = "Transfer money")
    public ResponseEntity<ApiResponse<Transaction>> transferMoney(@Valid @RequestBody TransferRequest request, Authentication authentication) {
        Transaction transaction = transferService.transferMoney(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", transaction));
    }
    
    @PostMapping("/beneficiaries")
    @Operation(summary = "Add beneficiary")
    public ResponseEntity<ApiResponse<Beneficiary>> addBeneficiary(@Valid @RequestBody AddBeneficiaryRequest request, Authentication authentication) {
        Beneficiary beneficiary = transferService.addBeneficiary(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary added successfully", beneficiary));
    }
    
    @GetMapping("/beneficiaries")
    @Operation(summary = "Get all beneficiaries")
    public ResponseEntity<ApiResponse<List<Beneficiary>>> getBeneficiaries(Authentication authentication) {
        List<Beneficiary> beneficiaries = transferService.getBeneficiaries(authentication);
        return ResponseEntity.ok(ApiResponse.success("Beneficiaries retrieved successfully", beneficiaries));
    }
    
    @PostMapping("/schedule")
    @Operation(summary = "Schedule a transfer")
    public ResponseEntity<ApiResponse<ScheduledTransfer>> scheduleTransfer(@Valid @RequestBody ScheduledTransferRequest request, Authentication authentication) {
        ScheduledTransfer scheduledTransfer = transferService.scheduleTransfer(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Transfer scheduled successfully", scheduledTransfer));
    }
}
