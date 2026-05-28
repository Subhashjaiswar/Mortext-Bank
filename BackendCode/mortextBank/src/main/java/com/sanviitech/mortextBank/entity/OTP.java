package com.sanviitech.mortextBank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTP {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phoneNumber;
    
    @Column(nullable = false, length = 10)
    private String otpCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OTPType otpType;
    
    @Column(nullable = false)
    private Boolean isUsed = false;
    
    @Column(nullable = false)
    private Boolean isExpired = false;
    
    @Column(nullable = false)
    private Integer attemptCount = 0;
    
    @Column(nullable = false)
    private LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum OTPType {
        REGISTRATION,
        LOGIN,
        FORGOT_PASSWORD,
        RESET_PASSWORD,
        TRANSACTION_VERIFICATION,
        BENEFICIARY_ADDITION,
        CARD_ACTIVATION
    }
}
