package com.sanviitech.mortextBank.constants;

public final class ValidationConstants {

    private ValidationConstants() {
        // Private constructor to prevent instantiation
    }

    // Common validation messages
    public static final String REQUIRED = " is required";
    public static final String MUST_BE_BETWEEN = " must be between ";
    public static final String AND_CHARACTERS = " and ";
    public static final String CHARACTERS = " characters";
    public static final String MUST_NOT_EXCEED = " must not exceed ";
    public static final String MUST_BE_AT_LEAST = " must be at least ";

    // Amount validation
    public static final String AMOUNT_REQUIRED = "Amount is required";
    public static final String AMOUNT_MIN = "Amount must be at least 1.00";
    public static final String AMOUNT_MAX = "Amount must not exceed 1,000,000.00";

    // Account number validation
    public static final String ACCOUNT_NUMBER_REQUIRED = "Account number is required";
    public static final String ACCOUNT_NUMBER_LENGTH = "Account number must be between 9 and 20 characters";
    public static final String ACCOUNT_NUMBER_DIGITS = "Account number must contain only digits";

    // Beneficiary type validation
    public static final String BENEFICIARY_TYPE_REQUIRED = "Beneficiary type is required";
    public static final String BENEFICIARY_TYPE_INVALID = "Beneficiary type must be one of: BANK_ACCOUNT, UPI_ID, WALLET";

    // Email validation
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Email should be valid";

    // IFSC validation
    public static final String IFSC_INVALID = "IFSC code must be in valid format (e.g., SBIN0001234)";

    // Name validation
    public static final String NAME_REQUIRED_TEMPLATE = "%s is required";
    public static final String NAME_LENGTH_TEMPLATE = "%s must be between %d and %d characters";

    // Phone number validation
    public static final String PHONE_NUMBER_REQUIRED = "Phone number is required";
    public static final String PHONE_NUMBER_INVALID = "Phone number must be a valid Indian mobile number";

    // Security pin validation
    public static final String SECURITY_PIN_REQUIRED = "Security pin is required";
    public static final String SECURITY_PIN_LENGTH = "Security pin must be between 4 and 6 digits";
    public static final String SECURITY_PIN_NUMERIC = "Security pin must be numeric";

    // Size validation
    public static final String SIZE_EXCEED_TEMPLATE = "%s must not exceed %d characters";
    public static final String SIZE_RANGE_TEMPLATE = "%s must be between %d and %d characters";

    // Transfer type validation
    public static final String TRANSFER_TYPE_REQUIRED = "Transfer type is required";
    public static final String TRANSFER_TYPE_INVALID = "Transfer type must be one of: IMPS, NEFT, RTGS, UPI, BANK_TRANSFER";

    // UPI validation
    public static final String UPI_INVALID = "UPI ID must be in valid format (e.g., user@bank)";

    // General error messages
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String INVALID_SECURITY_PIN = "Invalid security pin";
    public static final String CURRENT_SECURITY_PIN_INCORRECT = "Current security pin is incorrect";
    public static final String INVALID_OR_EXPIRED_OTP = "Invalid or expired OTP";
    public static final String OTP_EXPIRED = "OTP has expired";
    public static final String CARD_PIN_LOCKED = "Card PIN is locked. Please contact customer support.";
    public static final String INVALID_CURRENT_PIN = "Invalid current PIN";
    public static final String UNAUTHORIZED_ACCESS_TO_ACCOUNT = "Unauthorized access to account";
    public static final String UNAUTHORIZED_ACCESS_TO_CARD = "Unauthorized access to card";
    public static final String INSUFFICIENT_BALANCE = "Insufficient balance in account";
    public static final String PDF_GENERATION_FAILED = "PDF generation failed";

    // Resource not found messages
    public static final String RESOURCE_NOT_FOUND_TEMPLATE = "%s not found with %s: '%s'";
}
