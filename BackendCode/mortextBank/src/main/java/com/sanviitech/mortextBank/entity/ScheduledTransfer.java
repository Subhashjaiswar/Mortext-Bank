package com.sanviitech.mortextBank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTransfer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id", nullable = false)
    private Account fromAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 20)
    private String toAccountNumber;
    
    @Column(length = 100)
    private String toAccountName;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 500)
    private String remarks;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferType transferType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleType scheduleType;
    
    @Column(nullable = false)
    private LocalDateTime scheduledDate;
    
    @Column(nullable = false)
    private Boolean isRecurring = false;
    
    @Column(nullable = false)
    private Integer recurringIntervalDays = 0;
    
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status = TransferStatus.SCHEDULED;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum TransferType {
        BANK_TRANSFER,
        UPI_TRANSFER,
        QR_PAYMENT
    }
    
    public enum ScheduleType {
        ONE_TIME,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }
    
    public enum TransferStatus {
        SCHEDULED,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}
