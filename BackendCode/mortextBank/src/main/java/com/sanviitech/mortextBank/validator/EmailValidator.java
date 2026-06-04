package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public String validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationConstants.EMAIL_REQUIRED;
        }
        if (!isValid(email)) {
            return ValidationConstants.EMAIL_INVALID;
        }
        return null;
    }
}
