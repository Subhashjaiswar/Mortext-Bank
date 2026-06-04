package com.sanviitech.mortextBank.service.impl;

import java.time.LocalDateTime;

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
        String emailError = emailValidator.validate(request.getEmail());
        if (emailError != null) {
            throw GlobalException.badRequest(emailError);
        }

        String nameError = nameValidator.validate(request.getFullName(), "Full name");
        if (nameError != null) {
            throw GlobalException.badRequest(nameError);
        }

        String pinError = securityPinValidator.validate(request.getSecurityPin());
        if (pinError != null) {
            throw GlobalException.badRequest(pinError);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw GlobalException.badRequest(ValidationConstants.EMAIL_ALREADY_EXISTS);
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setSecurityPin(request.getSecurityPin());

        user = userRepository.save(user);
        System.out.println("User saved with ID: " + user.getId());

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

        System.out.println("Saving account with number: " + account.getAccountNumber());
        account = accountRepository.save(account);
        System.out.println("Account saved with ID: " + account.getId());

        String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(user.getEmail(), null, null));

        return new AuthResponse(token, "Bearer", user.getId(), user.getFullName(), user.getEmail(), "CUSTOMER", account.getAccountNumber());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String emailError = emailValidator.validate(request.getEmail());
        if (emailError != null) {
            throw GlobalException.badRequest(emailError);
        }

        String pinError = securityPinValidator.validate(request.getSecurityPin());
        if (pinError != null) {
            throw GlobalException.badRequest(pinError);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", request.getEmail()));

        if (!user.getSecurityPin().equals(request.getSecurityPin())) {
            throw GlobalException.badRequest(ValidationConstants.INVALID_SECURITY_PIN);
        }

        String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(user.getEmail(), null, null));

        String accountNumber = accountRepository.findByUserId(user.getId())
                .stream()
                .findFirst()
                .map(com.sanviitech.mortextBank.entity.Account::getAccountNumber)
                .orElse(null);

        return new AuthResponse(token, "Bearer", user.getId(), user.getFullName(), user.getEmail(), "CUSTOMER", accountNumber);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", request.getEmail()));

        String otp = OTPUtil.generateOTP();
        saveOTP(user.getEmail(), "", otp, OTP.OTPType.FORGOT_PASSWORD);
        emailService.sendOTPEmail(user.getEmail(), otp, "Security Pin Reset");
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        OTP otp = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(request.getEmail(), request.getOtp())
                .orElseThrow(() -> GlobalException.badRequest(ValidationConstants.INVALID_OR_EXPIRED_OTP));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw GlobalException.badRequest(ValidationConstants.OTP_EXPIRED);
        }

        User user = userRepository.findByEmail(otp.getEmail())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", otp.getEmail()));

        user.setSecurityPin(request.getNewSecurityPin());
        userRepository.save(user);

        otp.setIsUsed(true);
        otpRepository.save(otp);
    }

    @Override
    public void verifyOTP(OTPVerificationRequest request) {
        OTP otp = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(request.getEmail(), request.getOtp())
                .orElseThrow(() -> GlobalException.badRequest(ValidationConstants.INVALID_OR_EXPIRED_OTP));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw GlobalException.badRequest(ValidationConstants.OTP_EXPIRED);
        }

        otp.setIsUsed(true);
        otpRepository.save(otp);
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
    }
}