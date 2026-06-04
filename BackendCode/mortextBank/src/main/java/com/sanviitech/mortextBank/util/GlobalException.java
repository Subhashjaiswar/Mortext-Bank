package com.sanviitech.mortextBank.util;

import org.springframework.http.HttpStatus;

public class GlobalException extends RuntimeException {
    private final HttpStatus status;
    private final ErrorType errorType;

    public enum ErrorType {
        BAD_REQUEST,
        INSUFFICIENT_BALANCE,
        RESOURCE_NOT_FOUND,
        UNAUTHORIZED
    }

    public GlobalException(String message, HttpStatus status, ErrorType errorType) {
        super(message);
        this.status = status;
        this.errorType = errorType;
    }

    public GlobalException(String message, HttpStatus status, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorType = errorType;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    // Convenience methods for common error types
    public static GlobalException badRequest(String message) {
        return new GlobalException(message, HttpStatus.BAD_REQUEST, ErrorType.BAD_REQUEST);
    }

    public static GlobalException insufficientBalance(String message) {
        return new GlobalException(message, HttpStatus.BAD_REQUEST, ErrorType.INSUFFICIENT_BALANCE);
    }

    public static GlobalException resourceNotFound(String message) {
        return new GlobalException(message, HttpStatus.NOT_FOUND, ErrorType.RESOURCE_NOT_FOUND);
    }

    public static GlobalException resourceNotFound(String resourceName, String fieldName, Object fieldValue) {
        return new GlobalException(
            String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
            HttpStatus.NOT_FOUND,
            ErrorType.RESOURCE_NOT_FOUND
        );
    }

    public static GlobalException unauthorized(String message) {
        return new GlobalException(message, HttpStatus.UNAUTHORIZED, ErrorType.UNAUTHORIZED);
    }
}
