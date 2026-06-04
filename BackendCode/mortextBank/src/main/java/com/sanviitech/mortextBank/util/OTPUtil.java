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
        return String.valueOf(System.currentTimeMillis() + random.nextInt(100));
    }
    
    public static String generateCardNumber() {
        return java.util.stream.IntStream.range(0, 16)
                .map(i -> random.nextInt(10))
                .mapToObj(String::valueOf)
                .collect(java.util.stream.Collectors.joining());
    }
    
    public static String generateCVV() {
        return String.valueOf(100 + random.nextInt(900));
    }
}
