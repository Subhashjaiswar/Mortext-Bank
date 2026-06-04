package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class IFSCValidator {

    private static final Pattern IFSC_PATTERN = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

    public boolean isValid(String ifscCode) {
        if (ifscCode == null || ifscCode.trim().isEmpty()) {
            return true; // IFSC is optional
        }
        return IFSC_PATTERN.matcher(ifscCode.trim().toUpperCase()).matches();
    }

    public String validate(String ifscCode) {
        if (ifscCode == null || ifscCode.trim().isEmpty()) {
            return null; // IFSC is optional
        }
        if (!isValid(ifscCode)) {
            return ValidationConstants.IFSC_INVALID;
        }
        return null;
    }
}
