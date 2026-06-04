package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SecurityPinValidator {

    private static final Pattern PIN_PATTERN = Pattern.compile("^\\d{4,6}$");

    public boolean isValid(String securityPin) {
        if (securityPin == null || securityPin.trim().isEmpty()) {
            return false;
        }
        return PIN_PATTERN.matcher(securityPin.trim()).matches();
    }

    public String validate(String securityPin) {
        if (securityPin == null || securityPin.trim().isEmpty()) {
            return ValidationConstants.SECURITY_PIN_REQUIRED;
        }
        String trimmed = securityPin.trim();
        if (trimmed.length() < 4 || trimmed.length() > 6) {
            return ValidationConstants.SECURITY_PIN_LENGTH;
        }
        if (!PIN_PATTERN.matcher(trimmed).matches()) {
            return ValidationConstants.SECURITY_PIN_NUMERIC;
        }
        return null;
    }
}
