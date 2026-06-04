package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UPIValidator {

    private static final Pattern UPI_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+$");

    public boolean isValid(String upiId) {
        if (upiId == null || upiId.trim().isEmpty()) {
            return true; // UPI ID is optional
        }
        return UPI_PATTERN.matcher(upiId.trim()).matches();
    }

    public String validate(String upiId) {
        if (upiId == null || upiId.trim().isEmpty()) {
            return null; // UPI ID is optional
        }
        if (!isValid(upiId)) {
            return ValidationConstants.UPI_INVALID;
        }
        return null;
    }
}
