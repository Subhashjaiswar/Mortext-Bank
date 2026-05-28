package com.sanviitech.mortextBank.util;

import java.security.SecureRandom;

public class OTPUtil {
    
    private static final SecureRandom random = new SecureRandom();
    
    public static String generateOTP() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    public static String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + random.nextInt(1000);
    }
    
    public static String generateAccountNumber() {
        return "SAN" + System.currentTimeMillis() + random.nextInt(100);
    }
    
    public static String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (i > 0 && i % 4 == 0) {
                cardNumber.append(" ");
            }
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString().replace(" ", "");
    }
    
    public static String generateCVV() {
        return String.valueOf(100 + random.nextInt(900));
    }
}
