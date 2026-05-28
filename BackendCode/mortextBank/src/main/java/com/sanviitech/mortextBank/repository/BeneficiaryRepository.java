package com.sanviitech.mortextBank.repository;

import com.sanviitech.mortextBank.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    
    List<Beneficiary> findByUserId(Long userId);
    
    List<Beneficiary> findByUserIdAndIsActive(Long userId, Boolean isActive);
}
