package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberValidator {

    public boolean isValid(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return false;
        }
        String trimmed = accountNumber.trim();
        return trimmed.length() >= 9 && trimmed.length() <= 20 && trimmed.matches("\\d+");
    }

    public String validate(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return ValidationConstants.ACCOUNT_NUMBER_REQUIRED;
        }
        String trimmed = accountNumber.trim();
        if (trimmed.length() < 9 || trimmed.length() > 20) {
            return ValidationConstants.ACCOUNT_NUMBER_LENGTH;
        }
        if (!trimmed.matches("\\d+")) {
            return ValidationConstants.ACCOUNT_NUMBER_DIGITS;
        }
        return null;
    }
}
