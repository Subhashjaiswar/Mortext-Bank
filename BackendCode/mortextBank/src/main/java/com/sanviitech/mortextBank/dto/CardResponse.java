package com.sanviitech.mortextBank.dto;

import com.sanviitech.mortextBank.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    
    private Long id;
    private String cardNumber;
    private String cardHolderName;
    private Card.CardType cardType;
    private String cvv;
    private LocalDate expiryDate;
    private Boolean isActive;
    private Boolean isFrozen;
    private Boolean isVirtual;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
    private Integer pinAttempts;
    private Boolean pinLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate issuedDate;
}
