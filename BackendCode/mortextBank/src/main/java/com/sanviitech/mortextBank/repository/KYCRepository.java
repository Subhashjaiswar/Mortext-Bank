package com.sanviitech.mortextBank.repository;

import com.sanviitech.mortextBank.entity.KYC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KYCRepository extends JpaRepository<KYC, Long> {
    
    Optional<KYC> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    Page<KYC> findByStatus(KYC.KYCStatus status, Pageable pageable);
}
