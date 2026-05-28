package com.sanviitech.mortextBank.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTransferRequest {
    
    @NotBlank(message = "From account number is required")
    private String fromAccountNumber;
    
    @NotBlank(message = "To account number is required")
    private String toAccountNumber;
    
    @Size(max = 100, message = "To account name must not exceed 100 characters")
    private String toAccountName;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
    private BigDecimal amount;
    
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
    
    @NotBlank(message = "Transfer type is required")
    private String transferType;
    
    @NotBlank(message = "Schedule type is required")
    private String scheduleType;
    
    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;
    
    @NotNull(message = "Is recurring is required")
    private Boolean isRecurring;
    
    private Integer recurringIntervalDays;
    
    private LocalDateTime endDate;
}
