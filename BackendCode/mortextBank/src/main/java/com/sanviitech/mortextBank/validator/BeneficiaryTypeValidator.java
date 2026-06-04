package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BeneficiaryTypeValidator {

    private static final List<String> VALID_BENEFICIARY_TYPES = Arrays.asList(
        "BANK_ACCOUNT",
        "UPI_ID",
        "WALLET"
    );

    public boolean isValid(String beneficiaryType) {
        if (beneficiaryType == null || beneficiaryType.trim().isEmpty()) {
            return false;
        }
        return VALID_BENEFICIARY_TYPES.contains(beneficiaryType.trim().toUpperCase());
    }

    public String validate(String beneficiaryType) {
        if (beneficiaryType == null || beneficiaryType.trim().isEmpty()) {
            return ValidationConstants.BENEFICIARY_TYPE_REQUIRED;
        }
        if (!isValid(beneficiaryType)) {
            return ValidationConstants.BENEFICIARY_TYPE_INVALID;
        }
        return null;
    }
}
