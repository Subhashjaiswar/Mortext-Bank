package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

@Component
public class SizeValidator {

    public boolean isValid(String value, int maxLength) {
        if (value == null) {
            return true;
        }
        return value.trim().length() <= maxLength;
    }

    public String validate(String value, String fieldName, int maxLength) {
        if (value != null && value.trim().length() > maxLength) {
            return String.format(ValidationConstants.SIZE_EXCEED_TEMPLATE, fieldName, maxLength);
        }
        return null;
    }

    public boolean isValid(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    public String validate(String value, String fieldName, int minLength, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            return fieldName + ValidationConstants.REQUIRED;
        }
        int length = value.trim().length();
        if (length < minLength || length > maxLength) {
            return String.format(ValidationConstants.SIZE_RANGE_TEMPLATE, fieldName, minLength, maxLength);
        }
        return null;
    }
}
