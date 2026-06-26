package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

@Component
public class NameValidator {

    public boolean isValid(String name, int minLength, int maxLength) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() >= minLength && trimmed.length() <= maxLength;
    }

    public String validate(String name, String fieldName, int minLength, int maxLength) {
        if (name == null || name.trim().isEmpty()) {
            return String.format(ValidationConstants.NAME_REQUIRED_TEMPLATE, fieldName);
        }
        String trimmed = name.trim();
        if (trimmed.length() < minLength || trimmed.length() > maxLength) {
            return String.format(ValidationConstants.NAME_LENGTH_TEMPLATE, fieldName, minLength, maxLength);
        }
        return null;
    }

    public boolean isValid(String name) {
        return isValid(name, 2, 100);
    }

    public String validate(String name, String fieldName) {
        return validate(name, fieldName, 2, 100);
    }
}
