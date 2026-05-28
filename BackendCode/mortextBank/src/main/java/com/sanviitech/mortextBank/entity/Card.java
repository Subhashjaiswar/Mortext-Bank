package com.sanviitech.mortextBank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String cardNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(nullable = false, length = 100)
    private String cardHolderName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;
    
    @Column(nullable = false, length = 5)
    private String cvv;
    
    @Column(nullable = false)
    private LocalDate expiryDate;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isFrozen = false;
    
    @Column(nullable = false)
    private Boolean isVirtual = false;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal dailyLimit = new BigDecimal("50000");
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyLimit = new BigDecimal("500000");
    
    @Column(nullable = false)
    private Integer pinAttempts = 0;
    
    @Column(nullable = false)
    private Boolean pinLocked = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDate issuedDate;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum CardType {
        DEBIT,
        CREDIT
    }
}
