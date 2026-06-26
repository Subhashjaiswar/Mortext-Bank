package com.sanviitech.mortextBank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 100)
    private String beneficiaryName;
    
    @Column(nullable = false, length = 20)
    private String accountNumber;
    
    @Column(length = 50)
    private String ifscCode;
    
    @Column(length = 50)
    private String bankName;
    
    @Column(length = 50)
    private String branch;
    
    @Column(length = 50)
    private String upiId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BeneficiaryType beneficiaryType;
    
    @Column(length = 500)
    private String nickname;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum BeneficiaryType {
        BANK_ACCOUNT,
        UPI,
        WALLET
    }
}
