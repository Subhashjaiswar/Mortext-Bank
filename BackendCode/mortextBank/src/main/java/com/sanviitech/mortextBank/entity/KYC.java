package com.sanviitech.mortextBank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYC {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(length = 100)
    private String panNumber;
    
    @Column(length = 100)
    private String aadhaarNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdProofType idProofType;
    
    @Column(length = 100)
    private String idProofNumber;
    
    @Column(length = 500)
    private String idProofImageUrl;
    
    @Column(length = 500)
    private String addressProofImageUrl;
    
    @Column(length = 500)
    private String selfieImageUrl;
    
    @Column(length = 500)
    private String panCardImageUrl;
    
    @Column(length = 500)
    private String aadhaarCardImageUrl;
    
    @Column(length = 100)
    private String dateOfBirth;
    
    @Column(length = 500)
    private String permanentAddress;
    
    @Column(length = 500)
    private String currentAddress;
    
    @Column(length = 50)
    private String city;
    
    @Column(length = 50)
    private String state;
    
    @Column(length = 10)
    private String pincode;
    
    @Column(length = 50)
    private String nationality;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KYCStatus status = KYCStatus.PENDING;
    
    @Column(length = 500)
    private String rejectionReason;
    
    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();
    
    private LocalDateTime verifiedAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum IdProofType {
        AADHAAR,
        PAN,
        PASSPORT,
        DRIVING_LICENSE,
        VOTER_ID
    }
    
    public enum KYCStatus {
        PENDING,
        APPROVED,
        REJECTED,
        UNDER_REVIEW
    }
}
