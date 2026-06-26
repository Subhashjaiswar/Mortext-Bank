package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PhoneNumberValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern PHONE_WITH_COUNTRY_CODE = Pattern.compile("^\\+91[6-9]\\d{9}$");

    public boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        String trimmed = phoneNumber.trim();
        return PHONE_PATTERN.matcher(trimmed).matches() || PHONE_WITH_COUNTRY_CODE.matcher(trimmed).matches();
    }

    public String validate(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ValidationConstants.PHONE_NUMBER_REQUIRED;
        }
        if (!isValid(phoneNumber)) {
            return ValidationConstants.PHONE_NUMBER_INVALID;
        }
        return null;
    }

    public boolean isValid(String phoneNumber, boolean required) {
        if (!required && (phoneNumber == null || phoneNumber.trim().isEmpty())) {
            return true;
        }
        return isValid(phoneNumber);
    }

    public String validate(String phoneNumber, boolean required) {
        if (!required && (phoneNumber == null || phoneNumber.trim().isEmpty())) {
            return null;
        }
        return validate(phoneNumber);
    }
}
