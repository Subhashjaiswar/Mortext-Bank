package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

@Component
public class RequiredFieldValidator {

    public boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public String validate(String value, String fieldName) {
        if (!isValid(value)) {
            return fieldName + ValidationConstants.REQUIRED;
        }
        return null;
    }

    public boolean isValid(Object value) {
        return value != null;
    }

    public String validate(Object value, String fieldName) {
        if (!isValid(value)) {
            return fieldName + ValidationConstants.REQUIRED;
        }
        return null;
    }
}
