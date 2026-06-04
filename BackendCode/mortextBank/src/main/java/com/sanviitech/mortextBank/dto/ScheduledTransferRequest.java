package com.sanviitech.mortextBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTransferRequest {
    
    private String fromAccountNumber;
    
    private String toAccountNumber;
    
    private String toAccountName;
    
    private BigDecimal amount;
    
    private String remarks;
    
    private String transferType;
    
    private String scheduleType;
    
    private LocalDateTime scheduledDate;
    
    private Boolean isRecurring;
    
    private Integer recurringIntervalDays;
    
    private LocalDateTime endDate;
}
