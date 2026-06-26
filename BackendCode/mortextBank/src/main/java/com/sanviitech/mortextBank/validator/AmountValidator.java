package com.sanviitech.mortextBank.validator;

import com.sanviitech.mortextBank.constants.ValidationConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AmountValidator {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("1.00");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000000.00");

    public boolean isValid(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        return amount.compareTo(MIN_AMOUNT) >= 0 && amount.compareTo(MAX_AMOUNT) <= 0;
    }

    public String validate(BigDecimal amount) {
        if (amount == null) {
            return ValidationConstants.AMOUNT_REQUIRED;
        }
        if (amount.compareTo(MIN_AMOUNT) < 0) {
            return ValidationConstants.AMOUNT_MIN;
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            return ValidationConstants.AMOUNT_MAX;
        }
        return null;
    }

    public boolean isValid(BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount) {
        if (amount == null) {
            return false;
        }
        return amount.compareTo(minAmount) >= 0 && amount.compareTo(maxAmount) <= 0;
    }

    public String validate(BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount) {
        if (amount == null) {
            return ValidationConstants.AMOUNT_REQUIRED;
        }
        if (amount.compareTo(minAmount) < 0) {
            return ValidationConstants.MUST_BE_AT_LEAST + minAmount;
        }
        if (amount.compareTo(maxAmount) > 0) {
            return ValidationConstants.MUST_NOT_EXCEED + maxAmount;
        }
        return null;
    }
}
