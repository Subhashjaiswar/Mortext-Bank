package com.sanviitech.mortextBank.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendOTPEmail(String to, String otp, String otpType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Mortext Bank - OTP Verification");
        message.setText("Your OTP for " + otpType + " is: " + otp + "\n\nThis OTP is valid for 10 minutes.\n\nPlease do not share this OTP with anyone.");
        mailSender.send(message);
    }
    
    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Welcome to Mortext Bank");
        message.setText("Dear " + username + ",\n\nWelcome to Mortext Bank! Your account has been successfully created.\n\nThank you for choosing us.\n\nBest regards,\nMortext Bank Team");
        
        mailSender.send(message);
    }
    
    public void sendTransactionAlert(String to, String transactionId, String amount, String type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Transaction Alert - Mortext Bank");
        message.setText("A transaction has been " + type + " from your account.\n\nTransaction ID: " + transactionId + "\nAmount: " + amount + "\n\nIf you did not authorize this transaction, please contact us immediately.");
        
        mailSender.send(message);
    }
}
