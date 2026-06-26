package com.sanviitech.mortextBank.service.impl;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanviitech.mortextBank.dto.AuthResponse;
import com.sanviitech.mortextBank.dto.ForgotPasswordRequest;
import com.sanviitech.mortextBank.dto.LoginRequest;
import com.sanviitech.mortextBank.dto.OTPVerificationRequest;
import com.sanviitech.mortextBank.dto.RegisterRequest;
import com.sanviitech.mortextBank.dto.ResetPasswordRequest;
import com.sanviitech.mortextBank.entity.Account;
import com.sanviitech.mortextBank.entity.OTP;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.constants.ValidationConstants;
import com.sanviitech.mortextBank.util.GlobalException;
import com.sanviitech.mortextBank.repository.AccountRepository;
import com.sanviitech.mortextBank.repository.OTPRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import com.sanviitech.mortextBank.security.JwtTokenProvider;
import com.sanviitech.mortextBank.service.AuthService;
import com.sanviitech.mortextBank.util.EmailService;
import com.sanviitech.mortextBank.util.OTPUtil;
import com.sanviitech.mortextBank.validator.EmailValidator;
import com.sanviitech.mortextBank.validator.NameValidator;
import com.sanviitech.mortextBank.validator.SecurityPinValidator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OTPRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private NameValidator nameValidator;

    @Autowired
    private SecurityPinValidator securityPinValidator;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Registration attempt started for email: {}", request.getEmail());
        log.debug("Registration request details - Name: {}, Email: {}", request.getFullName(), request.getEmail());
        
        try {
            String emailError = emailValidator.validate(request.getEmail());
            if (emailError != null) {
                log.warn("Email validation failed for: {} - Error: {}", request.getEmail(), emailError);
                throw GlobalException.badRequest(emailError);
            }

            String nameError = nameValidator.validate(request.getFullName(), "Full name");
            if (nameError != null) {
                log.warn("Name validation failed for: {} - Error: {}", request.getFullName(), nameError);
                throw GlobalException.badRequest(nameError);
            }

            String pinError = securityPinValidator.validate(request.getSecurityPin());
            if (pinError != null) {
                log.warn("Security PIN validation failed for email: {} - Error: {}", request.getEmail(), pinError);
                throw GlobalException.badRequest(pinError);
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Registration attempt with existing email: {}", request.getEmail());
                throw GlobalException.badRequest(ValidationConstants.EMAIL_ALREADY_EXISTS);
            }

            User user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setSecurityPin(request.getSecurityPin());

            user = userRepository.save(user);
            log.info("User saved successfully with ID: {}", user.getId());

            // Create account for the user
            Account account = new Account();
            account.setAccountNumber(OTPUtil.generateAccountNumber());
            account.setUser(user);
            account.setAccountType(Account.AccountType.SAVINGS);
            account.setBalance(java.math.BigDecimal.ZERO);
            account.setAvailableBalance(java.math.BigDecimal.ZERO);
            account.setAccountName("Savings Account");
            account.setCurrency("INR");
            account.setIsActive(true);
            account.setIsFrozen(false);
            account.setAccountOpenedAt(java.time.LocalDateTime.now());

            log.debug("Creating account with number: {} for user: {}", account.getAccountNumber(), user.getId());
            account = accountRepository.save(account);
            log.info("Account saved successfully with ID: {} and number: {}", account.getId(), account.getAccountNumber());

            String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(user.getEmail(), null, null));
            log.debug("JWT token generated for user: {}", user.getEmail());

            long endTime = System.currentTimeMillis();
            log.info("Registration completed successfully for email: {} in {} ms", request.getEmail(), (endTime - startTime));
            
            return new AuthResponse(token, "Bearer", user.getId(), user.getFullName(), user.getEmail(), "CUSTOMER", account.getAccountNumber());
        } catch (Exception e) {
            log.error("Registration failed for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Login attempt started for email: {}", request.getEmail());
        
        try {
            String emailError = emailValidator.validate(request.getEmail());
            if (emailError != null) {
                log.warn("Login email validation failed for: {} - Error: {}", request.getEmail(), emailError);
                throw GlobalException.badRequest(emailError);
            }

            String pinError = securityPinValidator.validate(request.getSecurityPin());
            if (pinError != null) {
                log.warn("Login PIN validation failed for email: {} - Error: {}", request.getEmail(), pinError);
                throw GlobalException.badRequest(pinError);
            }

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.warn("Login attempt with non-existent email: {}", request.getEmail());
                        return GlobalException.resourceNotFound("User", "email", request.getEmail());
                    });

            if (!user.getSecurityPin().equals(request.getSecurityPin())) {
                log.warn("Invalid security PIN attempt for email: {}", request.getEmail());
                throw GlobalException.badRequest(ValidationConstants.INVALID_SECURITY_PIN);
            }

            String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(user.getEmail(), null, null));
            log.debug("JWT token generated for user: {}", user.getEmail());

            String accountNumber = accountRepository.findByUserId(user.getId())
                    .stream()
                    .findFirst()
                    .map(com.sanviitech.mortextBank.entity.Account::getAccountNumber)
                    .orElse(null);

            long endTime = System.currentTimeMillis();
            log.info("Login successful for email: {} in {} ms", request.getEmail(), (endTime - startTime));
            
            return new AuthResponse(token, "Bearer", user.getId(), user.getFullName(), user.getEmail(), "CUSTOMER", accountNumber);
        } catch (Exception e) {
            log.error("Login failed for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Forgot password request initiated for email: {}", request.getEmail());
        
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.warn("Forgot password attempt with non-existent email: {}", request.getEmail());
                        return GlobalException.resourceNotFound("User", "email", request.getEmail());
                    });

            String otp = OTPUtil.generateOTP();
            saveOTP(user.getEmail(), "", otp, OTP.OTPType.FORGOT_PASSWORD);
            emailService.sendOTPEmail(user.getEmail(), otp, "Security Pin Reset");
            
            long endTime = System.currentTimeMillis();
            log.info("OTP sent successfully for password reset to email: {} in {} ms", request.getEmail(), (endTime - startTime));
        } catch (Exception e) {
            log.error("Forgot password failed for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Password reset attempt for email: {}", request.getEmail());
        
        try {
            OTP otp = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(request.getEmail(), request.getOtp())
                    .orElseThrow(() -> {
                        log.warn("Invalid or expired OTP for password reset - Email: {}, OTP: {}", request.getEmail(), request.getOtp());
                        return GlobalException.badRequest(ValidationConstants.INVALID_OR_EXPIRED_OTP);
                    });

            if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
                log.warn("OTP expired for password reset - Email: {}, Expiry: {}", request.getEmail(), otp.getExpiryTime());
                throw GlobalException.badRequest(ValidationConstants.OTP_EXPIRED);
            }

            User user = userRepository.findByEmail(otp.getEmail())
                    .orElseThrow(() -> {
                        log.error("User not found during password reset - Email: {}", otp.getEmail());
                        return GlobalException.resourceNotFound("User", "email", otp.getEmail());
                    });

            user.setSecurityPin(request.getNewSecurityPin());
            userRepository.save(user);
            log.info("Security PIN updated successfully for user: {}", user.getEmail());

            otp.setIsUsed(true);
            otpRepository.save(otp);
            
            long endTime = System.currentTimeMillis();
            log.info("Password reset completed successfully for email: {} in {} ms", request.getEmail(), (endTime - startTime));
        } catch (Exception e) {
            log.error("Password reset failed for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void verifyOTP(OTPVerificationRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("OTP verification attempt for email: {}", request.getEmail());
        
        try {
            OTP otp = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(request.getEmail(), request.getOtp())
                    .orElseThrow(() -> {
                        log.warn("Invalid OTP verification attempt - Email: {}, OTP: {}", request.getEmail(), request.getOtp());
                        return GlobalException.badRequest(ValidationConstants.INVALID_OR_EXPIRED_OTP);
                    });

            if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
                log.warn("OTP expired during verification - Email: {}, Expiry: {}", request.getEmail(), otp.getExpiryTime());
                throw GlobalException.badRequest(ValidationConstants.OTP_EXPIRED);
            }

            otp.setIsUsed(true);
            otpRepository.save(otp);
            
            long endTime = System.currentTimeMillis();
            log.info("OTP verified successfully for email: {} in {} ms", request.getEmail(), (endTime - startTime));
        } catch (Exception e) {
            log.error("OTP verification failed for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    private void saveOTP(String email, String phoneNumber, String otpCode, OTP.OTPType otpType) {
        OTP otp = new OTP();
        otp.setEmail(email);
        otp.setPhoneNumber(phoneNumber);
        otp.setOtpCode(otpCode);
        otp.setOtpType(otpType);
        otp.setIsUsed(false);
        otp.setIsExpired(false);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        otpRepository.save(otp);
        log.debug("OTP saved successfully - Email: {}, Type: {}, Expiry: {}", email, otpType, otp.getExpiryTime());
    }
}