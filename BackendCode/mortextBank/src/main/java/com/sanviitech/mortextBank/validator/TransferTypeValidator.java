package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TransferTypeValidator {

    private static final List<String> VALID_TRANSFER_TYPES = Arrays.asList(
        "IMPS",
        "NEFT",
        "RTGS",
        "UPI",
        "BANK_TRANSFER"
    );

    public boolean isValid(String transferType) {
        if (transferType == null || transferType.trim().isEmpty()) {
            return false;
        }
        return VALID_TRANSFER_TYPES.contains(transferType.trim().toUpperCase());
    }

    public String validate(String transferType) {
        if (transferType == null || transferType.trim().isEmpty()) {
            return ValidationConstants.TRANSFER_TYPE_REQUIRED;
        }
        if (!isValid(transferType)) {
            return ValidationConstants.TRANSFER_TYPE_INVALID;
        }
        return null;
    }
}
