package com.sanviitech.mortextBank.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    
    @NotBlank(message = "From account number is required")
    private String fromAccountNumber;
    
    @NotBlank(message = "To account number is required")
    private String toAccountNumber;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
    @DecimalMax(value = "1000000.00", message = "Amount must not exceed 1,000,000.00")
    private BigDecimal amount;
    
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
    
    @NotBlank(message = "Transfer type is required")
    private String transferType;
    
    private String beneficiaryId;
}
