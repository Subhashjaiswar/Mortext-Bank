package com.sanviitech.mortextBank.repository;

import com.sanviitech.mortextBank.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    
    Optional<OTP> findByEmailAndOtpCodeAndIsUsedFalse(String email, String otpCode);
    
    Optional<OTP> findByPhoneNumberAndOtpCodeAndIsUsedFalse(String phoneNumber, String otpCode);
    
    List<OTP> findByEmailAndOtpType(String email, OTP.OTPType otpType);
    
    List<OTP> findByPhoneNumberAndOtpType(String phoneNumber, OTP.OTPType otpType);
}
